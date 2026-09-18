package io.github.rontyamc.lucentics.common.beam.transfer;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface IFluidAcceptor {
    int fill(FluidStack stack, boolean simulate);

    static IFluidAcceptor of(IFluidHandler handler) {
        return (stack, simulate) ->
                handler.fill(stack, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
    }
}
