package io.github.rontyamc.lucentics.blocks.prism.prism_ritual;

import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class PrismRitualBehavior extends PrismBehavior {
    public static final PrismRitualBehavior INSTANCE = new PrismRitualBehavior();

    private PrismRitualBehavior() {}

    @Override
    public void activate(ServerLevel level, BlockPos prismPos, BlockPos interactPos) {

    }
}
