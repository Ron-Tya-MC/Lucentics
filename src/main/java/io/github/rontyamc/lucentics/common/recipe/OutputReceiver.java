package io.github.rontyamc.lucentics.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public interface OutputReceiver {
    default ItemStack acceptItem(ItemStack stack, boolean simulate) {
        return stack.copy();
    }
    default FluidStack acceptFluid(FluidStack stack, boolean simulate) {
        return stack.copy();
    }
}
