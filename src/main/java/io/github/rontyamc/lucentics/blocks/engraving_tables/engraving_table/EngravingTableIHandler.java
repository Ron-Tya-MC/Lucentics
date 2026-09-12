package io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table;

import io.github.rontyamc.lucentics.common.util.ItemUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class EngravingTableIHandler implements IItemHandler {
    private final EngravingTableBehavior behavior;

    public EngravingTableIHandler(EngravingTableBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public int getSlots() {
        return Math.min(behavior.getBuffer().size(), behavior.getMaxBufferSize().get()) + 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == 0) return behavior.getContainer().asItemOrEmpty();
        int index = slot - 1;
        return behavior.getBufferAt(index);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack itemStack, boolean simulate) {
        if (slot != 0) return itemStack;
        if (!behavior.getContainer().isEmpty() && !behavior.getBlockMerge()) return itemStack;

        return behavior.insert(itemStack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == 0) return behavior.hasOutputItem() ? behavior.extract(amount, simulate) : ItemStack.EMPTY;

        int index = slot - 1;
        return behavior.extractBufferAt(index, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return behavior.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack itemStack) {
        if (slot != 0) return false;
        return behavior.getContainer().isEmpty() || ItemUtil.isSameItem(behavior.getContainer().asItemOrEmpty(), itemStack, false);
    }
}
