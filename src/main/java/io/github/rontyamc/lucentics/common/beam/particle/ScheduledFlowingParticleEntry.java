package io.github.rontyamc.lucentics.common.beam.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rontyamc.lucentics.client.particle.FlowingGlowParticleOptions;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ScheduledFlowingParticleEntry(BlockPos pos, FlowingGlowParticleOptions options) {
    public static final Codec<ScheduledFlowingParticleEntry> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(ScheduledFlowingParticleEntry::pos),
            FlowingGlowParticleOptions.CODEC.fieldOf("options").forGetter(ScheduledFlowingParticleEntry::options)
    ).apply(ins, ScheduledFlowingParticleEntry::new));

    public static final Codec<Map<BlockPos, ScheduledFlowingParticleEntry>> MAP_CODEC = CODEC.listOf().xmap(
            list -> {
                Map<BlockPos, ScheduledFlowingParticleEntry> map = new HashMap<>();
                for (ScheduledFlowingParticleEntry entry : list) map.put(entry.pos(), entry);
                return map;
            },
            map -> List.copyOf(map.values())
    );
}