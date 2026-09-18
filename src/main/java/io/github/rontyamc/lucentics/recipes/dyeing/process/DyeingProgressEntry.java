package io.github.rontyamc.lucentics.recipes.dyeing.process;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record DyeingProgressEntry(BlockPos pos, ResourceLocation itemId, String color, int processingTime) {
    public static final Codec<DyeingProgressEntry> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(DyeingProgressEntry::pos),
            ResourceLocation.CODEC.fieldOf("item_id").forGetter(DyeingProgressEntry::itemId),
            Codec.STRING.fieldOf("color").forGetter(DyeingProgressEntry::color),
            Codec.INT.fieldOf("processing_time").forGetter(DyeingProgressEntry::processingTime)
    ).apply(ins, DyeingProgressEntry::new));

    public static final Codec<Map<BlockPos, DyeingProgressEntry>> MAP_CODEC = CODEC.listOf().xmap(
            list -> {
                Map<BlockPos, DyeingProgressEntry> map = new HashMap<>();
                for (DyeingProgressEntry entry : list) map.put(entry.pos(), entry);
                return map;
            },
            map -> List.copyOf(map.values())
    );
}
