package io.github.rontyamc.lucentics.common.beam.transfer;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public interface IItemAcceptor {
    ItemStack insert(ItemStack stack, boolean simulate);

    static IItemAcceptor of(IItemHandler handler) {
        return (stack, simulate) -> ItemHandlerHelper.insertItemStacked(handler, stack, simulate);
    }
}
