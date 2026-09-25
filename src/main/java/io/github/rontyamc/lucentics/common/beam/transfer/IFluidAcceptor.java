package io.github.rontyamc.lucentics.common.beam.transfer;

import io.github.rontyamc.lucentics.blocks.prism.IPrismBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Optional;

public interface IFluidAcceptor {
    int acceptFluids(ServerLevel level, BlockPos acceptorPos, FluidStack stack, boolean simulate);

    static IFluidAcceptor ofHandler(IFluidHandler handler) {
        return (level, acceptorPos, stack, simulate) ->
                handler.fill(stack, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
    }

    static Optional<IFluidAcceptor> get(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof IPrismBehavior prism)) return Optional.empty();
        if (!(prism.getPrismBehavior() instanceof IFluidAcceptor iFluidAcceptor)) return Optional.empty();
        return Optional.of(iFluidAcceptor);
    }
}
