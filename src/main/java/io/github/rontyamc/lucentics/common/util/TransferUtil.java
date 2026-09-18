package io.github.rontyamc.lucentics.common.util;

import io.github.rontyamc.lucentics.common.beam.transfer.ImportingContexts.ItemImportingContext;
import io.github.rontyamc.lucentics.common.beam.transfer.ImportingContexts.FluidImportingContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Optional;

public final class TransferUtil {
    // 共用


    // アイテム
    public static Optional<IItemHandler> getItemHandler(ServerLevel level, BlockPos pos) {
        return Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.DOWN));
    }

    public static boolean hasItem(IItemHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            if (!handler.getStackInSlot(slot).isEmpty()) return true;
        }
        return false;
    }

    public static ItemImportingContext firstNonEmpty(IItemHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty()) return ItemImportingContext.of(handler, slot, stack);
        }
        return ItemImportingContext.EMPTY;
    }

    public static ItemImportingContext firstImportable(IItemHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                ItemStack simulated = handler.extractItem(slot, 1, true);
                if (!simulated.isEmpty()) return ItemImportingContext.of(handler, slot, stack);
            }
        }
        return ItemImportingContext.EMPTY;
    }

    // 流体
    public static Optional<IFluidHandler> getFluidHandler(ServerLevel level, BlockPos pos) {
        return Optional.ofNullable(level.getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.DOWN));
    }

    public static FluidImportingContext firstNonEmpty(IFluidHandler handler) {
        for (int tank = 0; tank < handler.getTanks(); tank++) {
            FluidStack stack = handler.getFluidInTank(tank);
            if (!stack.isEmpty()) return FluidImportingContext.of(handler, tank, stack);
        }
        return FluidImportingContext.EMPTY;
    }

    public static FluidImportingContext firstImportable(IFluidHandler handler) {
        for (int tank = 0; tank < handler.getTanks(); tank++) {
            FluidStack stack = handler.getFluidInTank(tank);
            if (!stack.isEmpty()) {
                FluidStack simulated = handler.drain(tank, IFluidHandler.FluidAction.SIMULATE);
                if (!simulated.isEmpty()) return FluidImportingContext.of(handler, tank, stack);
            }
        }
        return FluidImportingContext.EMPTY;
    }

    public static boolean hasFluid(IFluidHandler handler) {
        for (int tank = 0; tank < handler.getTanks(); tank++) {
            if (!handler.getFluidInTank(tank).isEmpty()) return true;
        }
        return false;
    }
}
