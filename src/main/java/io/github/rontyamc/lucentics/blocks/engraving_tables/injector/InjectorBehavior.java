package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.ItemUtilities;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class InjectorBehavior extends BlockEntityBehavior implements Clearable {
    public static final BehaviorType<InjectorBehavior> TYPE = new BehaviorType<>("injector");

    private ItemStack container;
    private ItemStack buffer;
    private Supplier<Integer> maxStackSize;
    public InjectorIHandler iHandler;
    private boolean blockMerge = true;

    private int processingTime = -1;
    private boolean recipeCheck = false;

    public InjectorBehavior(BaseBlockEntity be) {
        super(be);

        maxStackSize = () -> 64;
        setBlockMerge(false);
        iHandler = new InjectorIHandler(this);
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

    public ItemStack getBuffer() {
        return buffer == null ? ItemStack.EMPTY : buffer;
    }

    public List<ItemStack> getContents() {
        List<ItemStack> list = new ArrayList<>();

        list.addLast(container == null ? ItemStack.EMPTY : container);
        list.addLast(buffer == null ? ItemStack.EMPTY : buffer);
        return list;
    }

    @Override
    public void clearContent() {
        container = ItemStack.EMPTY;
        buffer = ItemStack.EMPTY;
        setIdle();
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getWorld();
        if (level == null || level.isClientSide()) return;

        if (container.isEmpty()) {
            setIdle();
            if (!buffer.isEmpty()) {
                flushBuffer();
            }
            return;
        }

        if (!isIdle()) {
            if (hasRecipe(level)) {
                processingTime--;
                blockEntity.setChanged();
                if (processingTime <= 0) {
                    craftOnce(level);
                }
            } else {
                setIdle();
            }
        } else if (recipeCheck) {
            if (hasRecipe(level)) {
                startProcessing(level);
            }
        }
    }

    private void craftOnce(Level level) {
        Optional<RecipeHolder<InjectorRecipe>> recipeHolder = getCurrentRecipe(level);
        if (recipeHolder.isEmpty()) {
            setIdle();
            return;
        }
        ItemStack result = recipeHolder.get().value().getResultItem(level.registryAccess());

        container.shrink(1);

        if (buffer.isEmpty()) {
            buffer = result.copy();
        } else {
            buffer.grow(result.getCount());
        }

        if (container.isEmpty()) {
            flushBuffer();
            setIdle();
        } else if (hasRecipe(level)) {
            startProcessing(level);
        } else {
            setIdle();
        }

        blockEntity.updated();
    }

    private void startProcessing(Level level) {
        processingTime = getCurrentRecipe(level)
                .map(r -> r.value().getProcessingDuration())
                .orElse(-1);
        if (processingTime <= 0) {
            processingTime = -1;
        }
    }

    private void flushBuffer() {
        container = buffer;
        buffer = ItemStack.EMPTY;
        blockEntity.updated();
    }

    private void setIdle() {
        processingTime = -1;
    }

    public boolean isIdle() {
        return processingTime == -1;
    }

    private boolean hasRecipe(Level level) {
        int daylight = getCurrentRecipe(level)
                .map(r -> r.value().getDayLightCondition())
                .orElse(0);
        return getCurrentRecipe(level).isPresent() && getDaylight(level, getPos()) >= daylight;
    }

    private Optional<RecipeHolder<InjectorRecipe>> getCurrentRecipe(Level level) {
        if (level == null || container.isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(
                LucenticsRecipeTypesRegister.INJECTION_TYPE.get(),
                new InjectorRecipeInput(container),
                level
        );
    }

    public static int getDaylight(Level level, BlockPos pos) {
        int i = level.getBrightness(LightLayer.SKY, pos) - level.getSkyDarken();
        float f = level.getSunAngle(1.0F);

        if (i > 0) {
            float f1 = f < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
            f += (f1 - f) * 0.2F;
            i = Math.round((float)i * Mth.cos(f));
        }

        return Mth.clamp(i, 0, 15);
    }

    public void notifyInserted() {
        this.recipeCheck = true;
    }

    public int getRemainingSpace() {
        int max = maxStackSize.get();
        if (container.isEmpty()) return max;
        return Math.min(max, container.getMaxStackSize()) - container.getCount();
    }

    public int getSlotLimit() {
        int limit = container.isEmpty() ? 64 : container.getMaxStackSize();
        return Math.min(maxStackSize.get(), limit);
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

    public ItemStack extractBuffer(int amount, boolean simulate) {
        if (buffer.isEmpty()) return ItemStack.EMPTY;

        ItemStack copyStack = buffer.copy();
        ItemStack extracted = copyStack.split(amount);

        if (!simulate) {
            buffer = copyStack;
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
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if (!container.isEmpty()) nbt.put("container", container.save(registries, new CompoundTag()));
        if (!buffer.isEmpty()) nbt.put("buffer", buffer.save(registries, new CompoundTag()));
        nbt.putInt("processing_time", processingTime);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        container = nbt.contains("container") ? ItemStack.parse(registries, nbt.getCompound("container")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        buffer = nbt.contains("buffer") ? ItemStack.parse(registries, nbt.getCompound("buffer")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        processingTime = nbt.getInt("processing_time");
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }
}
