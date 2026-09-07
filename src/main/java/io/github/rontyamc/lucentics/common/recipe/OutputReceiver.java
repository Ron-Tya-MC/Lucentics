package io.github.rontyamc.lucentics.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public interface OutputReceiver {
    void acceptItem(ItemStack stack);
    void acceptFluid(FluidStack stack);
}
