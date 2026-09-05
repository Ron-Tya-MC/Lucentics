package io.github.rontyamc.lucentics.blocks.pedestals;

import io.github.rontyamc.lucentics.common.util.ItemUtilities;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class PedestalIHandler implements IItemHandler {
    private final PedestalBehavior behavior;

    public PedestalIHandler(PedestalBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return behavior.getContent();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack itemStack, boolean simulate) {
        if (slot != 0) return itemStack;
        if (!behavior.getContent().isEmpty() && !behavior.getBlockMerge()) return itemStack;

        return behavior.insert(itemStack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) { return behavior.extract(amount, simulate); }

    @Override
    public int getSlotLimit(int slot) {
        return behavior.getSlotLimit();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack itemStack) {
        return behavior.getContent().isEmpty() || ItemUtilities.isSameItem(behavior.getContent(), itemStack, false);
    }
}
