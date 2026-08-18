package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class InjectorIHandler implements IItemHandler {
    private InjectorBehavior behavior;

    public InjectorIHandler(InjectorBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return behavior.getContainer();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack itemStack, boolean simulate) {
        if (!behavior.getContainer().isEmpty() && behavior.getBlockMerge()) return itemStack;

        ItemStack returnStack = behavior.insert(itemStack, simulate);
        if (!simulate && returnStack != itemStack) {
            behavior.blockEntity.updated();
        }
        return returnStack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (behavior.getContainer() == null) {
            return ItemStack.EMPTY;
        }
        ItemStack returnStack = behavior.extract(amount, simulate);
        if (!simulate && returnStack != ItemStack.EMPTY) {
            behavior.blockEntity.updated();
        }
        return returnStack;
    }

    @Override
    public int getSlotLimit(int i) {
        return behavior.getSlotLimit();
    }

    @Override
    public boolean isItemValid(int i, ItemStack itemStack) {
        return false;
    }
}
