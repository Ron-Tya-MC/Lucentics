package io.github.rontyamc.lucentics.blocks.pedestals;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.beam.INodeDevice;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PedestalBehavior extends BlockEntityBehavior implements INodeDevice, Clearable {
    public static final BehaviorType<PedestalBehavior> TYPE = new BehaviorType<>("pedestal");
    private static final int MAX_STACK_SIZE = 64;

    private ItemStack content = ItemStack.EMPTY;

    public PedestalBehavior(BaseBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    @Override
    public ItemStack getContent() {
        return content;
    }

    @Override
    public void clearContent() {
        content = ItemStack.EMPTY;
    }

    public int getRemainingSpace() {
        if (content.isEmpty()) return MAX_STACK_SIZE;
        return Math.min(MAX_STACK_SIZE, content.getMaxStackSize()) - content.getCount();
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!content.isEmpty() && !io.github.rontyamc.lucentics.common.ItemUtilities.isSameItem(content, stack, false)) {
            return stack;
        }

        int remainingSpace = getRemainingSpace();
        if (remainingSpace <= 0) return stack;

        int insertCount = Math.min(remainingSpace, stack.getCount());
        ItemStack returnStack = stack.copyWithCount(stack.getCount() - insertCount);

        if (!simulate) {
            if (content.isEmpty()) {
                content = stack.copyWithCount(insertCount);
            } else {
                content.grow(insertCount);
            }
            blockEntity.updated();
        }

        return returnStack;
    }

    public ItemStack extract(int amount, boolean simulate) {
        if (content.isEmpty()) return ItemStack.EMPTY;

        ItemStack copyStack = content.copy();
        ItemStack extracted = copyStack.split(amount);

        if (!simulate) {
            content = copyStack;
            blockEntity.updated();
        }

        return extracted;
    }

    public void dropContents(Level level, BlockPos pos) {
        Vec3 vec = getCenter(pos);
        ItemStack content = getContent();

        if (!content.isEmpty()) {
            Containers.dropItemStack(level, vec.x, vec.y, vec.z, content);
        }
        clearContent();
    }

    @Override
    public void consumeItem(int amount) {
        if (content.isEmpty()) return;
        content.shrink(amount);
        blockEntity.updated();
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if (!content.isEmpty()) {
            nbt.put("content", content.save(registries, new CompoundTag()));
        }
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        content = nbt.contains("content")
                ? ItemStack.parse(registries, nbt.getCompound("content")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }
}