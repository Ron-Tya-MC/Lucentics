package io.github.rontyamc.lucentics.common.util;

import io.github.rontyamc.lucentics.client.particle.FlowingGlowParticleOptions;
import io.github.rontyamc.lucentics.common.beam.node.BeamNode;
import io.github.rontyamc.lucentics.common.beam.particle.BeamParticles;
import io.github.rontyamc.lucentics.common.beam.particle.IFlowingParticleSchedulable;
import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class ParticleUtil {
    public static void spawnAndScheduleFlowing(ServerLevel level, BlockPos containerFrom, BlockPos nodeFrom, BeamNode target, Colors color, int cooldown) {
        FlowingGlowParticleOptions options = BeamParticles.buildOptions(containerFrom, nodeFrom, target, color);

        BeamParticles.spawnFlowing(level, options, nodeFrom, cooldown);

        if (level.getBlockState(nodeFrom).getBlock() instanceof IFlowingParticleSchedulable schedulable) {
            schedulable.registerContext(level, nodeFrom, options);
            schedulable.registerSchedule(level, nodeFrom, IFlowingParticleSchedulable.SCHEDULE_ID, schedulable.getTicker().getParticleLinger());
        }
    }
}
