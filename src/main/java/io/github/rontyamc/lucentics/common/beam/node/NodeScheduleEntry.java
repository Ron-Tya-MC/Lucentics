package io.github.rontyamc.lucentics.common.beam.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record NodeScheduleEntry(Map<ResourceLocation, Long> scheduleMap, BlockPos nodePos) {
    public static final Codec<NodeScheduleEntry> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.LONG).fieldOf("scheduleMap").forGetter(NodeScheduleEntry::scheduleMap),
            BlockPos.CODEC.fieldOf("node_pos").forGetter(NodeScheduleEntry::nodePos)
    ).apply(ins, NodeScheduleEntry::new));

    public static final Codec<Map<BlockPos, NodeScheduleEntry>> MAP_CODEC = CODEC.listOf().xmap(
            list -> {
                Map<BlockPos, NodeScheduleEntry> map = new HashMap<>();
                for (NodeScheduleEntry entry : list) map.put(entry.nodePos(), entry);
                return map;
            },
            map -> List.copyOf(map.values())
    );

    public NodeScheduleEntry(Map<ResourceLocation, Long> scheduleMap, BlockPos nodePos) {
        this.scheduleMap = new HashMap<>(scheduleMap);
        this.nodePos = nodePos;
    }
}
