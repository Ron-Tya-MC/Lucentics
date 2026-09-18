package io.github.rontyamc.lucentics.common.beam.transfer;

import io.github.rontyamc.lucentics.common.beam.node.BeamNode;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class ExportingContexts {
    public record ItemExportingContext(List<ItemExportingInfo> infos, ItemStack leftover) {
        public record ItemExportingInfo(IItemAcceptor acceptor, ItemStack stack, BeamNode targetNode) {
            public void commit() {
                acceptor.insert(stack, false);
            }
        }

        public boolean hasAnyExport() {
            return !infos.isEmpty();
        }

        public void commit() {
            for (ItemExportingInfo info : infos) info.commit();
        }
    }

    public record FluidExportingContext(List<FluidExportingInfo> infos, FluidStack leftover) {
        public record FluidExportingInfo(IFluidAcceptor acceptor, FluidStack stack, BeamNode targetNode) {
            public void commit() {
                acceptor.fill(stack, false);
            }
        }

        public boolean hasAnyExport() {
            return !infos.isEmpty();
        }

        public void commit() {
            for (FluidExportingInfo info : infos) info.commit();
        }
    }
}
