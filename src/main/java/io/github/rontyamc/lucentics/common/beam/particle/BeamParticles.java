package io.github.rontyamc.lucentics.common.beam.particle;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.client.particle.FlowingGlowParticleOptions;
import io.github.rontyamc.lucentics.common.beam.node.BeamNode;
import io.github.rontyamc.lucentics.common.beam.node.NodeScheduleHelper;
import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public class BeamParticles {
    private BeamParticles() {}

    public static final int FLOWING_DURATION = 20;
    private static final int PARTICLE_AMOUNT = 1;

    public static final ResourceLocation FLOWING_COOLDOWN = Lucentics.defaultLocation("flowing_cooldown");

    private static final RandomSource RANDOM_SOURCE = RandomSource.create();

    public static boolean canSpawnOnThisTick(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);

        long cooldown = NodeScheduleHelper.getTimer(chunk, pos, FLOWING_COOLDOWN);

        if (cooldown <= level.getGameTime()) {
            NodeScheduleHelper.remove(chunk, pos, FLOWING_COOLDOWN);
            return true;
        }
        else return false;
    }

    public static void spawnFlowing(ServerLevel level, FlowingGlowParticleOptions options, BlockPos savePos, int cooldown) {
        float d = RANDOM_SOURCE.nextFloat() * 0.2f - 0.1f;

        level.sendParticles(options, options.waypoints().getFirst().x(), options.waypoints().getFirst().y(), options.waypoints().getFirst().z(),
                PARTICLE_AMOUNT, d, d, d, 0);

        if (cooldown > 1) {
            LevelChunk chunk = level.getChunkAt(savePos);
            NodeScheduleHelper.append(chunk, savePos, FLOWING_COOLDOWN, level.getGameTime() + cooldown, true);
        }
    }

    public static void spawnFlowing(ServerLevel level, FlowingGlowParticleOptions options, BlockPos savePos) {
        float d = RANDOM_SOURCE.nextFloat() * 0.2f - 0.1f;

        level.sendParticles(options, options.waypoints().getFirst().x(), options.waypoints().getFirst().y(), options.waypoints().getFirst().z(),
                PARTICLE_AMOUNT, d, d, d, 0);
    }

    public static FlowingGlowParticleOptions buildOptions(BlockPos containerFrom, BlockPos nodeFrom, BeamNode target, Colors color, Vec3 delta) {
        BlockPos nodeTo = target.pos();
        BlockPos containerTo = target.isEndpoint() ? target.pos() : target.pos().above();

        NonNullList<Vec3> waypoints = NonNullList.create();

        waypoints.add(Vec3.atCenterOf(containerFrom));
        waypoints.add(Vec3.atCenterOf(nodeFrom));
        waypoints.add(Vec3.atCenterOf(nodeTo));
        waypoints.add(Vec3.atCenterOf(containerTo));

        int rgb = color.getColorCode();
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;

        return new FlowingGlowParticleOptions(waypoints, r, g, b, FLOWING_DURATION, RANDOM_SOURCE.nextFloat() * 0.3f + 0.05f, delta);
    }

    public static FlowingGlowParticleOptions buildOptions(BlockPos containerFrom, BlockPos nodeFrom, BeamNode target, Colors color) {
        float delta = RANDOM_SOURCE.nextFloat() * 0.2f - 0.1f;

        return buildOptions(containerFrom, nodeFrom, target, color, new Vec3(delta, delta, delta));
    }
}
