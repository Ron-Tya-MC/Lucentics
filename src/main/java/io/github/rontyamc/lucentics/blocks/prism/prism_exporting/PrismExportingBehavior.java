package io.github.rontyamc.lucentics.blocks.prism.prism_exporting;

import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.common.beam.node.IExportable;
import io.github.rontyamc.lucentics.common.beam.node.NodeActivationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class PrismExportingBehavior extends PrismBehavior implements IExportable {
    public static final PrismExportingBehavior INSTANCE = new PrismExportingBehavior();

    protected PrismExportingBehavior() {}

    @Override
    public void activate(ServerLevel level, BlockPos prismPos, BlockPos interactPos, NodeActivationContext context) {}
}
