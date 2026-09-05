package io.github.rontyamc.lucentics.recipes.crushing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record CrushingProgressEntry(BlockPos pos, ResourceLocation recipeId, int hits) {
    public static final Codec<CrushingProgressEntry> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(CrushingProgressEntry::pos),
            ResourceLocation.CODEC.fieldOf("recipeId").forGetter(CrushingProgressEntry::recipeId),
            Codec.INT.fieldOf("hits").forGetter(CrushingProgressEntry::hits)
    ).apply(ins, CrushingProgressEntry::new));

    public static final Codec<Map<BlockPos, CrushingProgressEntry>> MAP_CODEC = CODEC.listOf().xmap(
            list -> {
                Map<BlockPos, CrushingProgressEntry> map = new HashMap<>();
                for (CrushingProgressEntry entry : list) map.put(entry.pos(), entry);
                return map;
            },
            map -> List.copyOf(map.values())
    );
}
