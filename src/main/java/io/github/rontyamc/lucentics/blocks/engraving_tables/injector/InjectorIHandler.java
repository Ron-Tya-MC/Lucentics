package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import io.github.rontyamc.lucentics.common.ItemUtilities;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class InjectorIHandler implements IItemHandler {
    private final InjectorBehavior behavior;

    public InjectorIHandler(InjectorBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public int getSlots() {
        return 2;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? behavior.getContainer() : behavior.getBuffer();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack itemStack, boolean simulate) {
        if (slot != 0) return itemStack;
        if (behavior.hasOutputItem()) return itemStack;
        if (!behavior.getContainer().isEmpty() && !behavior.getBlockMerge()) return itemStack;

        return behavior.insert(itemStack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == 1) return behavior.extractBuffer(amount, simulate);

        if (behavior.getContainer() == null) return ItemStack.EMPTY;
        if (!behavior.hasOutputItem()) return ItemStack.EMPTY;

        return behavior.extract(amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot == 0) return behavior.getSlotLimit();
        return behavior.getBufferSlotLimit();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack itemStack) {
        if (slot != 0) return false;
        return behavior.getContainer().isEmpty() || ItemUtilities.isSameItem(behavior.getContainer(), itemStack, false);
    }
}
