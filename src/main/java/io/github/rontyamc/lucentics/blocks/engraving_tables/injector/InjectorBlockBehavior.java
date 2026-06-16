package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.ItemUtilities;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class InjectorBlockBehavior extends BlockEntityBehavior implements Clearable {
    public static final BehaviorType<InjectorBlockBehavior> TYPE = new BehaviorType<>("injector");

    ItemStack container;
    List<ItemStack> buffer;
    Supplier<Integer> maxStackSize;
    public InjectorIHandler iHandler;
    boolean blockMerge;

    public InjectorBlockBehavior(BaseBlockEntity be) {
        super(be);

        maxStackSize = () -> 1;
        setBlockMerge(false);
    }

    public void setBlockMerge(boolean blockMerge) {
        this.blockMerge = blockMerge;
    }

    public boolean getBlockMerge() {
        return blockMerge;
    }

    @Override
    public BehaviorType<?> getType() {
        return null;
    }

    @Override
    public void clearContent() {

    }

    @Override
    public void tick() {
        super.tick();

        for(Iterator<ItemStack> iterator = buffer.iterator(); iterator.hasNext();) {
            ItemStack stack = iterator.next();
            Level level = blockEntity.getLevel();

            if(container == null) {
                container = stack;
            }
            else {
                if (!ItemUtilities.canStackItems(container, stack)) {
                    Vec3 vec = getCenter(blockEntity.getBlockPos());
                    Containers.dropItemStack(level, vec.x, vec.y, vec.z, container);
                }
                else{
                    container.grow(stack.getCount());
                }
            }
            iterator.remove();
            blockEntity.updated();
        }
    }

    public ItemStack getContent() {
        return container == null ? ItemStack.EMPTY : container;
    }

    public int getStackSize() {
        int size = 0;
        size += getContent().getCount();
        return size;
    }

    public int getRemainingSpace() {
        int size = getStackSize();
        for (ItemStack stack : buffer) {
            size += stack.getCount();
        }
        int maxStack = Math.min(maxStackSize.get(), container.getMaxStackSize());
        return maxStack - size;
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        int remainingSpace = getRemainingSpace();
        if (remainingSpace <= 0) return stack;
        if (container != null && !ItemUtilities.isSameItem(container, stack, false)) return stack;

        ItemStack returnStack = ItemStack.EMPTY;

        if (stack.getCount() > remainingSpace) {
            returnStack = stack.copyWithCount(stack.getCount() - remainingSpace);
            if (!simulate) {
                ItemStack copyStack = stack.copyWithCount(remainingSpace);
                if (container != null) {
                    buffer.add(copyStack);
                }
                else {
                    container = copyStack;
                }
            }
        }
        else {
            if (!simulate) {
                if (container != null) {
                    buffer.add(stack);
                }
                else {
                    container = stack;
                }
            }
        }
        return returnStack;
    }

    public ItemStack extract(int amount, boolean simulate) {
        ItemStack container = this.container;
        ItemStack copyStack = container.copy();
        ItemStack extractedStack = copyStack.split(amount);
        if (!simulate) {
            this.container = copyStack;
            if (copyStack.isEmpty()) {
                this.container = null;
            }
        }
        return extractedStack;
    }
}
