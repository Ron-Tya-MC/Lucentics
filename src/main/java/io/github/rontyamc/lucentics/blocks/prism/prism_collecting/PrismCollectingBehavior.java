package io.github.rontyamc.lucentics.blocks.prism.prism_collecting;

import io.github.rontyamc.lucentics.blocks.prism.prism_io.PrismIOBehavior;
import io.github.rontyamc.lucentics.common.beam.node.IStopExplore;
import io.github.rontyamc.lucentics.common.beam.node.NodeActivationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class PrismCollectingBehavior extends PrismIOBehavior implements IStopExplore {
    public static final PrismCollectingBehavior INSTANCE = new PrismCollectingBehavior();

    protected PrismCollectingBehavior() {
        super();
    }

    @Override
    public int getParticleLinger() {
        return super.getParticleLinger();
    }

    @Override
    public void activate(ServerLevel level, BlockPos prismPos, BlockPos interactPos, NodeActivationContext context) {
        super.activate(level, prismPos, interactPos, context);
    }

    @Override
    public void tickSchedule(ServerLevel level, BlockPos prismPos) {
        super.tickSchedule(level, prismPos);
    }
}
