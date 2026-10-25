package io.github.rontyamc.lucentics.blocks.emitter;

import io.github.rontyamc.lucentics.items.LensItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class EmitterIHandler implements IItemHandler {
    private final EmitterBehavior behavior;

    public EmitterIHandler(EmitterBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return behavior.getLensContainer();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack itemStack, boolean simulate) {
        if (!behavior.getLensContainer().isEmpty()) return itemStack;
        if (!(itemStack.getItem() instanceof LensItem)) return itemStack;

        return behavior.insert(itemStack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return behavior.extract(amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return behavior.getSlotLimit();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof LensItem)) return false;
        return behavior.getLensContainer().isEmpty();
    }
}
