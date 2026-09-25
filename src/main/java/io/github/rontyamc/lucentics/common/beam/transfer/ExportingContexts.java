package io.github.rontyamc.lucentics.common.beam.transfer;

import io.github.rontyamc.lucentics.common.beam.node.BeamNode;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class ExportingContexts {
    public record ItemExportingContext(List<ItemExportingInfo> infos, IItemAcceptor.Result fallbacks) {
        public static final ItemExportingContext EMPTY = new ItemExportingContext(List.of(), IItemAcceptor.Result.EMPTY);

        public record ItemExportingInfo(IItemAcceptor acceptor, ItemStack stack, BeamNode targetNode, ServerLevel level) {
            public IItemAcceptor.Result commit() {
                return acceptor.acceptItems(level, targetNode.pos(), stack, false);
            }
        }

        public boolean hasAnyExport() {
            return !infos.isEmpty();
        }

        public List<IItemAcceptor.Result> commit() {
            List<IItemAcceptor.Result> results = new ArrayList<>();
            for (ItemExportingInfo info : infos) results.add(info.commit());
            return results;
        }
    }

    public record FluidExportingContext(List<FluidExportingInfo> infos, FluidStack leftover) {
        public static final FluidExportingContext EMPTY = new FluidExportingContext(List.of(), FluidStack.EMPTY);

        public record FluidExportingInfo(IFluidAcceptor acceptor, FluidStack stack, BeamNode targetNode, ServerLevel level) {
            public FluidStack commit() {
                return stack.copyWithAmount(acceptor.acceptFluids(level, targetNode.pos(), stack, false));
            }
        }

        public boolean hasAnyExport() {
            return !infos.isEmpty();
        }

        public List<FluidStack> commit() {
            List<FluidStack> results = new ArrayList<>();
            for (FluidExportingInfo info : infos) results.add(info.commit());
            return results;
        }
    }
}
