package io.github.rontyamc.lucentics.common.util;

import io.github.rontyamc.lucentics.common.beam.transfer.ImportingContexts.FluidImportingContext;
import io.github.rontyamc.lucentics.common.beam.transfer.ImportingContexts.ItemImportingContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.Optional;
import java.util.function.Consumer;

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

    public static void insertOrConsume(IItemHandler handler, ItemStack stack, Consumer<ItemStack> leftoverConsumer) {
        ItemStack simulated = ItemHandlerHelper.insertItemStacked(handler, stack, true);
        int accepted = stack.getCount() - simulated.getCount();
        if (accepted <= 0) leftoverConsumer.accept(stack.copy());

        ItemStack rejected = ItemHandlerHelper.insertItemStacked(handler, stack.copyWithCount(accepted), false);
        ItemStack leftover = rejected.copyWithCount(rejected.getCount() + simulated.getCount());
        leftoverConsumer.accept(leftover.copy());
    }

    // 流体
    public static Optional<IFluidHandler> getFluidHandler(ServerLevel level, BlockPos pos) {
        return Optional.ofNullable(level.getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.DOWN));
    }

    public static Optional<IFluidHandlerItem> getFluidHandlerItem(ItemStack stack) {
        return Optional.ofNullable(stack.copyWithCount(1).getCapability(Capabilities.FluidHandler.ITEM));
    }

    public static FluidImportingContext firstImportable(IFluidHandler handler) {
        return firstImportableWithAmount(handler, 1);
    }

    public static FluidImportingContext firstImportableWithAmount(IFluidHandler handler, int amount) {
        for (int tank = 0; tank < handler.getTanks(); tank++) {
            FluidStack stack = handler.getFluidInTank(tank);
            if (!stack.isEmpty() && stack.getAmount() >= amount) {
                FluidStack simulated = handler.drain(stack.copyWithAmount(amount), IFluidHandler.FluidAction.SIMULATE);
                if (!simulated.isEmpty() && simulated.getAmount() >= amount) return FluidImportingContext.of(handler, tank, stack);
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

    public static void insertOrConsume(IFluidHandler handler, FluidStack stack, Consumer<FluidStack> leftoverConsumer) {
        int simulated = handler.fill(stack, IFluidHandler.FluidAction.SIMULATE);
        if (simulated <= 0) leftoverConsumer.accept(stack.copy());

        int accepted = handler.fill(stack.copyWithAmount(simulated), IFluidHandler.FluidAction.EXECUTE);
        FluidStack leftover = stack.copyWithAmount(stack.getAmount() - accepted);
        leftoverConsumer.accept(leftover.copy());
    }
}
