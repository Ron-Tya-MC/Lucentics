package io.github.rontyamc.lucentics.blocks.prism.prism_io;

import io.github.rontyamc.lucentics.blocks.prism.prism_importing.PrismImportingBehavior;
import io.github.rontyamc.lucentics.common.beam.node.IExportable;
import io.github.rontyamc.lucentics.common.beam.node.NodeActivationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class PrismIOBehavior extends PrismImportingBehavior implements IExportable {
    public static final PrismIOBehavior INSTANCE = new PrismIOBehavior();

    protected PrismIOBehavior() {
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
