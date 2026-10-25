package io.github.rontyamc.lucentics.common.beam.transfer;

import io.github.rontyamc.lucentics.blocks.prism.IPrismBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.Optional;

public interface IItemAcceptor {
    record Result(ItemStack leftover, List<ItemStack> byproducts) {
        public static final Result EMPTY = new Result(ItemStack.EMPTY, List.of());

        public static Result of(ItemStack leftover) {
            return new Result(leftover, List.of());
        }
    }

    Result acceptItems(ServerLevel level, BlockPos acceptorPos, ItemStack stack, boolean simulate);

    static IItemAcceptor ofHandler(IItemHandler handler) {
        return (level, acceptorPos, stack, simulate) ->
                Result.of(ItemHandlerHelper.insertItemStacked(handler, stack, simulate));
    }

    static Optional<IItemAcceptor> get(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof IPrismBehavior prism)) return Optional.empty();
        if (!(prism.getPrismBehavior() instanceof IItemAcceptor iItemAcceptor)) return Optional.empty();
        return Optional.of(iItemAcceptor);
    }
}
