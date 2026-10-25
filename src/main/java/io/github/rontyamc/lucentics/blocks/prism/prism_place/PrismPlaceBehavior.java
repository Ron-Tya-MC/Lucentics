package io.github.rontyamc.lucentics.blocks.prism.prism_place;

import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.common.beam.node.NodeActivationContext;
import io.github.rontyamc.lucentics.common.beam.transfer.IItemAcceptor;
import io.github.rontyamc.lucentics.common.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;

public class PrismPlaceBehavior extends PrismBehavior implements IItemAcceptor {
    public static final PrismPlaceBehavior INSTANCE = new PrismPlaceBehavior();

    protected PrismPlaceBehavior() {}

    @Override
    public void activate(ServerLevel level, BlockPos prismPos, BlockPos interactPos, NodeActivationContext context) {}

    @Override
    public IItemAcceptor.Result acceptItems(ServerLevel level, BlockPos acceptorPos, ItemStack stack, boolean simulate) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return Result.of(stack.copy());
        BlockPlaceContext context = new DirectionalPlaceContext(level, acceptorPos.above(), Direction.DOWN, stack, Direction.UP);

        boolean canPlace = BlockUtil.canPlaceBlock(blockItem, context);
        if (!canPlace) return Result.of(stack.copy());

        if (simulate) {
            return Result.of(stack.copyWithCount(stack.getCount() - 1));
        }
        else {
            return blockItem.place(context).consumesAction() ? Result.of(stack.copyWithCount(stack.getCount() - 1)) : Result.of(stack.copy());
        }
    }
}
