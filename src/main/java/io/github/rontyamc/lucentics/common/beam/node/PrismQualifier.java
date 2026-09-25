package io.github.rontyamc.lucentics.common.beam.node;

import io.github.rontyamc.lucentics.blocks.prism.IPrismBehavior;
import io.github.rontyamc.lucentics.common.beam.transfer.IFluidAcceptor;
import io.github.rontyamc.lucentics.common.beam.transfer.IItemAcceptor;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

@FunctionalInterface
public interface PrismQualifier {
    boolean test(ServerLevel level, BlockPos prismPos);

    PrismQualifier EXPORT_TARGET = (level, prismPos) -> {
        var state = level.getBlockState(prismPos);
        if (!(state.getBlock() instanceof IPrismBehavior prism)) return false;
        return prism.getPrismBehavior() instanceof IExportable;
    };

    PrismQualifier ITEM_ACCEPTOR = (level, prismPos) -> IItemAcceptor.get(level, prismPos).isPresent();

    PrismQualifier FLUID_ACCEPTOR = (level, prismPos) -> IFluidAcceptor.get(level, prismPos).isPresent();

    PrismQualifier STOP_EXPLORE = (level, prismPos) -> {
        var state = level.getBlockState(prismPos);
        if (!(state.getBlock() instanceof IPrismBehavior prism)) return false;
        return prism.getPrismBehavior() instanceof IStopExplore;
    };

    PrismQualifier PRISM_RITUAL = (level, prismPos) ->
            level.getBlockState(prismPos).is(LucenticsTagRegister.LucenticsBTags.PRISM_RITUAL.tag);
}
