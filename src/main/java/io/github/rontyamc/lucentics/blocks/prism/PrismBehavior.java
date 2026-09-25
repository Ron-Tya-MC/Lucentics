package io.github.rontyamc.lucentics.blocks.prism;

import io.github.rontyamc.lucentics.common.beam.node.NodeActivationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

public abstract class PrismBehavior {
    public static final RandomSource RANDOM_SOURCE = RandomSource.create();

    public abstract void activate(ServerLevel level, BlockPos prismPos, BlockPos interactPos, NodeActivationContext context);
}