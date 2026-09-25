package io.github.rontyamc.lucentics.common.beam.transfer;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class ImportingContexts {
    public record ItemImportingContext(IItemHandler handler, int slot, ItemStack itemStack) {
        public final static ItemImportingContext EMPTY = new ItemImportingContext(null, -1, ItemStack.EMPTY);

        public static ItemImportingContext of(IItemHandler handler, int slot, ItemStack itemStack) {
            return new ItemImportingContext(handler, slot, itemStack);
        }

        public boolean isEmpty() {
            return this.handler == null || this.slot == -1 || this.itemStack.isEmpty();
        }
    }

    public record FluidImportingContext(IFluidHandler handler, int tank, FluidStack fluidStack) {
        public final static FluidImportingContext EMPTY = new FluidImportingContext(null, -1, FluidStack.EMPTY);

        public static FluidImportingContext of(IFluidHandler handler, int tank, FluidStack fluidStack) {
            return new FluidImportingContext(handler, tank, fluidStack);
        }

        public boolean isEmpty() {
            return this.handler == null || this.tank == -1 || this.fluidStack.isEmpty();
        }
    }
}
