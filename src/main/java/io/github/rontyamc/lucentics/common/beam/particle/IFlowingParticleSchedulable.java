package io.github.rontyamc.lucentics.common.beam.particle;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.client.particle.FlowingGlowParticleOptions;
import io.github.rontyamc.lucentics.common.beam.node.ISchedulable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public interface IFlowingParticleSchedulable extends ISchedulable {
    ResourceLocation SCHEDULE_ID = Lucentics.defaultLocation("flowing_particle");

    @Override
    IFlowingParticleScheduleTicker getTicker();

    void registerContext(ServerLevel level, BlockPos pos, FlowingGlowParticleOptions options);
}