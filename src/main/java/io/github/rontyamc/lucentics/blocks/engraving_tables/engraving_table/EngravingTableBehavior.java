package io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorIHandler;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRecipe;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRecipeInput;
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

import static io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBehavior.getDaylight;

public class EngravingTableBehavior extends ReceiveBehavior implements Clearable {
    public static final BehaviorType<EngravingTableBehavior> TYPE = new BehaviorType<>("engraving_table");

    private ItemStack container = ItemStack.EMPTY;
    private final List<ItemStack> buffer = new ArrayList<>();
    private boolean hasOutputItem = false;
    private Supplier<Integer> maxStackSize;
    public EngravingTableIHandler iHandler;
    private boolean blockMerge;
    private boolean metDayLightCondition;

    private int processingTime = -1;

    public EngravingTableBehavior(BaseBlockEntity be) {
        super(be);

        hasOutputItem = false;
        metDayLightCondition = true;
        maxStackSize = () -> 64;
        setBlockMerge(true);
        iHandler = new EngravingTableIHandler(this);
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

    public ItemStack getBuffetAt(int index) {
        if (index < 0 || index >= buffer.size()) return ItemStack.EMPTY;
        return buffer.get(index) == null ? ItemStack.EMPTY : buffer.get(index);
    }

    public List<ItemStack> collectBuffer() {
        List<ItemStack> collected = new ArrayList<>(buffer);
        buffer.clear();
        blockEntity.updated();
        return collected;
    }

    public boolean hasOutputItem() {
        return hasOutputItem;
    }

    public boolean metDayLightCondition() {
        return metDayLightCondition;
    }

    public List<ItemStack> getContents() {
        List<ItemStack> list = new ArrayList<>();

        list.addLast(getContainer());
        for (ItemStack stack : buffer) {
            list.addLast(stack == null ? ItemStack.EMPTY : stack);
        }
        return list;
    }

    @Override
    public void clearContent() {
        container = ItemStack.EMPTY;
        buffer.clear();
        hasOutputItem = false;
        setIdle();
    }

    public int getRemainingSpace() {
        int max = maxStackSize.get();
        if (getContainer().isEmpty()) return max;
        return Math.min(max, getContainer().getMaxStackSize()) - getContainer().getCount();
    }

    public int getSlotLimit(int slot) {
        int limit;
        if (slot == 0) limit = getContainer().isEmpty() ? 64 : getContainer().getMaxStackSize();
        else limit = getBuffetAt(slot - 1).isEmpty() ? 64 : getBuffetAt(slot - 1).getMaxStackSize();
        return Math.min(maxStackSize.get(), limit);
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!getContainer().isEmpty() && !ItemUtilities.isSameItem(getContainer(), stack, false)) return stack;

        int remainingSpace = getRemainingSpace();
        if (remainingSpace <= 0) return stack;

        int insertCount = Math.min(remainingSpace, stack.getCount());
        ItemStack returnStack = stack.copyWithCount(stack.getCount() - insertCount);

        if (!simulate) {
            if (getContainer().isEmpty()) {
                container = stack.copyWithCount(insertCount);
            } else {
                container.grow(insertCount);
            }
            hasOutputItem = false;
            blockEntity.updated();
        }

        return returnStack;
    }

    public ItemStack extract(int amount, boolean simulate) {
        if (getContainer().isEmpty()) return ItemStack.EMPTY;

        ItemStack copyStack = getContainer().copy();
        ItemStack extracted = copyStack.split(amount);

        if (!simulate) {
            container = copyStack;
            blockEntity.updated();
        }

        return extracted;
    }

    public ItemStack extractBufferAt(int index, int amount, boolean simulate) {
        if (getBuffetAt(index).isEmpty()) return ItemStack.EMPTY;

        ItemStack copyStack = getBuffetAt(index).copy();
        ItemStack extracted = copyStack.split(amount);

        if (!simulate) {
            if (copyStack.isEmpty()) {
                buffer.remove(index);
            } else {
                buffer.set(index, copyStack);
            }
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

        List<Beam> beams = new ArrayList<>(getTrails().stream().toList());
        trails.clear();

        if (getContainer().isEmpty()) {
            setIdle();
            metDayLightCondition = true;
            if (!buffer.isEmpty()) {
                flushBuffer();
            }
            return;
        }

        EngravingTableRecipeInput input = new EngravingTableRecipeInput(getContainer(), beams);

        if (!isIdle()) {
            if (checkDayLightCondition(serverLevel, input) && hasRecipe(serverLevel, input)) {
                processingTime--;
                blockEntity.setChanged();
                if (processingTime > 0) return;

                Optional<RecipeHolder<EngravingTableRecipe>> recipeHolder = getCurrentRecipe(serverLevel, input);
                if (recipeHolder.isEmpty()) {
                    setIdle();
                } else {
                    EngravingTableRecipe recipe = recipeHolder.get().value();
                    craft(serverLevel, recipe, input);
                }
            } else {
                setIdle();
            }
        } else if (checkDayLightCondition(serverLevel, input) && hasRecipe(serverLevel, input)) {
            startProcessing(serverLevel, input);
        }
    }

    private void flushBuffer() {
        container = buffer.getFirst();
        buffer.removeFirst();
        hasOutputItem = true;
        blockEntity.updated();
    }
    private void setIdle() {
        processingTime = -1;
    }

    public boolean isIdle() {
        return processingTime == -1;
    }

    private boolean hasRecipe(ServerLevel level, EngravingTableRecipeInput input) {
        if (getCurrentRecipe(level, input).isEmpty()) {
            metDayLightCondition = true;
            return false;
        }
        else return true;
    }

    private boolean checkDayLightCondition(ServerLevel level, EngravingTableRecipeInput input) {
        int daylight = getCurrentRecipe(level, input)
                .map(r -> r.value().getDayLightCondition())
                .orElse(0);
        metDayLightCondition = getDaylight(level, getPos()) >= daylight;
        return metDayLightCondition;
    }

    private Optional<RecipeHolder<EngravingTableRecipe>> getCurrentRecipe(ServerLevel level, EngravingTableRecipeInput input) {
        if (level == null || input.isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(LucenticsRecipeTypesRegister.ENGRAVING_TYPE.get(), input, level);
    }

    private void craft(ServerLevel level, EngravingTableRecipe recipe, EngravingTableRecipeInput input) {
        Optional<List<EngravingTableRecipe.ConsumptionEntry>> consumption = recipe.resolveConsumptionCached(input, level);
        if (consumption.isEmpty()) return;

        if (recipe.getMainInput().isPresent()) container.shrink(recipe.getMainInput().get().count());
        for (EngravingTableRecipe.ConsumptionEntry entry : consumption.get()) {
            entry.device().consumeItem(entry.amount());
        }

        for (var output : recipe.getArguments().outputs()) {
            output.item().ifPresent(item -> ItemUtilities.stackOrAppend(buffer, item));
        }

        if (getContainer().isEmpty()) {
            flushBuffer();
            setIdle();
        } else if (hasRecipe(level, input)) {
            startProcessing(level, input);
        } else {
            setIdle();
        }

        blockEntity.updated();
    }

    private void startProcessing(ServerLevel level, EngravingTableRecipeInput input) {
        processingTime = getCurrentRecipe(level, input)
                .map(r -> r.value().getProcessingDuration())
                .orElse(-1);
        if (processingTime <= 0) {
            processingTime = -1;
        }
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if (!getContainer().isEmpty()) {
            nbt.put("container", getContainer().save(registries, new CompoundTag()));
        }
        ListTag bufferList = new ListTag();
        for (ItemStack stack : buffer) {
            if (!stack.isEmpty()) bufferList.add(stack.save(registries, new CompoundTag()));
        }
        nbt.put("buffer", bufferList);
        nbt.putInt("processing_time", processingTime);
        nbt.putBoolean("has_output_item", hasOutputItem);
        nbt.putBoolean("met_daylight_condition", metDayLightCondition);
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
        hasOutputItem = nbt.getBoolean("has_output_item");
        metDayLightCondition = nbt.getBoolean("met_daylight_condition");
    }

    @Override
    public boolean isSafeNBT() { return true; }
}
