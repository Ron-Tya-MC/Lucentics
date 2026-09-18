package io.github.rontyamc.lucentics.common.beam.particle;

import io.github.rontyamc.lucentics.client.particle.FlowingGlowParticleOptions;
import io.github.rontyamc.lucentics.registers.LucenticsAttachmentRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;

public class ScheduledFlowingParticleHelper {
    public static ScheduledFlowingParticleEntry get(LevelChunk chunk, BlockPos pos) {
        return chunk.getData(LucenticsAttachmentRegister.SCHEDULED_FLOWING).get(pos.immutable());
    }

    public static ScheduledFlowingParticleEntry set(LevelChunk chunk, BlockPos pos, FlowingGlowParticleOptions options) {
        return chunk.getData(LucenticsAttachmentRegister.SCHEDULED_FLOWING).put(pos.immutable(),
                new ScheduledFlowingParticleEntry(pos.immutable(), options));
    }

    public static void clear(LevelChunk chunk, BlockPos pos) {
        chunk.getData(LucenticsAttachmentRegister.SCHEDULED_FLOWING).remove(pos.immutable());
    }
}
