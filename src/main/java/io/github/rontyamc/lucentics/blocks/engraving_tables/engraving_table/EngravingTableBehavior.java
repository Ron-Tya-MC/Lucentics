package io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.ItemUtilities;
import io.github.rontyamc.lucentics.common.beam.Beam;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.ReceiveBehavior;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class EngravingTableBehavior extends ReceiveBehavior implements Clearable {
    public static final BehaviorType<EngravingTableBehavior> TYPE = new BehaviorType<>("engraving_table");

    private ItemStack container = ItemStack.EMPTY;
    private final List<ItemStack> buffer = new ArrayList<>();
    private Supplier<Integer> maxStackSize;
    private boolean blockMerge = true;

    private int processingTime = -1;
    private boolean recipeCheck = false;

    public EngravingTableBehavior(BaseBlockEntity be) {
        super(be);

        maxStackSize = () -> 64;
        setBlockMerge(false);
        clearContent();
    }

    public void setBlockMerge(boolean blockMerge) {
        this.blockMerge = blockMerge;
    }

    public boolean getBlockMerge() {
        return blockMerge;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    public ItemStack getContainer() {
        return container == null ? ItemStack.EMPTY : container;
    }

    public List<ItemStack> getBuffer() {
        return buffer;
    }

    public List<ItemStack> collectBuffer() {
        List<ItemStack> collected = new ArrayList<>(buffer);
        buffer.clear();
        blockEntity.updated();
        return collected;
    }

    public List<ItemStack> getContents() {
        List<ItemStack> list = new ArrayList<>();

        list.addLast(container == null ? ItemStack.EMPTY : container);
        for (ItemStack stack : buffer) {
            list.addLast(stack == null ? ItemStack.EMPTY : stack);
        }
        return list;
    }

    @Override
    public void clearContent() {
        container = ItemStack.EMPTY;
        buffer.clear();
        setIdle();
    }

    public void notifyInserted() {
        this.recipeCheck = true;
    }

    public int getRemainingSpace() {
        int max = maxStackSize.get();
        if (container.isEmpty()) return max;
        return Math.min(max, container.getMaxStackSize()) - container.getCount();
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!container.isEmpty() && !ItemUtilities.isSameItem(container, stack, false)) return stack;

        int remainingSpace = getRemainingSpace();
        if (remainingSpace <= 0) return stack;

        int insertCount = Math.min(remainingSpace, stack.getCount());
        ItemStack returnStack = stack.copyWithCount(stack.getCount() - insertCount);

        if (!simulate) {
            if (container.isEmpty()) {
                container = stack.copyWithCount(insertCount);
            } else {
                container.grow(insertCount);
            }
            notifyInserted();
            blockEntity.updated();
        }

        return returnStack;
    }

    public ItemStack extract(int amount, boolean simulate) {
        if (container.isEmpty()) return ItemStack.EMPTY;

        ItemStack copyStack = container.copy();
        ItemStack extracted = copyStack.split(amount);

        if (!simulate) {
            container = copyStack;
            blockEntity.updated();
        }

        return extracted;
    }

    public void dropContents(Level level, BlockPos pos) {
        Vec3 vec = getCenter(pos);
        List<ItemStack> contents = getContents();

        if (!contents.isEmpty()) {
            for (ItemStack stack : contents) {
                Containers.dropItemStack(level, vec.x, vec.y, vec.z, stack);
            }
        }
        clearContent();
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getWorld();
        if (!(level instanceof ServerLevel serverLevel)) return;

        List<Beam> beams = new ArrayList<>(getTrails().stream().map(b -> b).toList());
        trails.clear();

        if (container.isEmpty()) {
            setIdle();
            return;
        }

        EngravingTableRecipeInput input = new EngravingTableRecipeInput(container, beams);
        Optional<RecipeHolder<EngravingTableRecipe>> recipeHolder =
                serverLevel.getRecipeManager().getRecipeFor(LucenticsRecipeTypesRegister.ENGRAVING_TYPE.get(), input, serverLevel);

        if (recipeHolder.isEmpty()) {
            setIdle();
            return;
        }

        EngravingTableRecipe recipe = recipeHolder.get().value();
        if (isIdle()) {
            processingTime = recipe.getProcessingDuration();
        }
        processingTime--;
        blockEntity.setChanged();

        if (processingTime <= 0) {
            craft(recipe, input, serverLevel);
            setIdle();
        }
    }
    private void setIdle() {
        processingTime = -1;
    }

    public boolean isIdle() {
        return processingTime == -1;
    }

    private void craft(EngravingTableRecipe recipe, EngravingTableRecipeInput input, ServerLevel level) {
        Optional<List<EngravingTableRecipe.ConsumptionEntry>> consumption = recipe.resolveConsumptionCached(input, level);
        if (consumption.isEmpty()) return;

        if (recipe.getMainInput().isPresent()) container.shrink(recipe.getMainInput().get().count());
        for (EngravingTableRecipe.ConsumptionEntry entry : consumption.get()) {
            entry.device().consumeItem(entry.amount());
        }

        for (var output : recipe.getArguments().outputs()) {
            output.item().ifPresent(item -> buffer.add(item.copy()));
        }
        blockEntity.updated();
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if (!container.isEmpty()) {
            nbt.put("container", container.save(registries, new CompoundTag()));
        }
        ListTag bufferList = new ListTag();
        for (ItemStack stack : buffer) {
            if (!stack.isEmpty()) bufferList.add(stack.save(registries, new CompoundTag()));
        }
        nbt.put("buffer", bufferList);
        nbt.putInt("processing_time", processingTime);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        container = nbt.contains("container")
                ? ItemStack.parse(registries, nbt.getCompound("container")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;

        buffer.clear();
        ListTag bufferList = nbt.getList("buffer", net.minecraft.nbt.Tag.TAG_COMPOUND);
        for (int i = 0; i < bufferList.size(); i++) {
            ItemStack.parse(registries, bufferList.getCompound(i)).ifPresent(buffer::add);
        }

        processingTime = nbt.getInt("processing_time");
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }
}
