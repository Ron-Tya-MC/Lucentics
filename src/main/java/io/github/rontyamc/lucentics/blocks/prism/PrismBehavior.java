package io.github.rontyamc.lucentics.blocks.prism;

import io.github.rontyamc.lucentics.common.beam.node.NodeActivationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public abstract class PrismBehavior {
    public abstract void activate(ServerLevel level, BlockPos prismPos, BlockPos interactPos, NodeActivationContext context);
}