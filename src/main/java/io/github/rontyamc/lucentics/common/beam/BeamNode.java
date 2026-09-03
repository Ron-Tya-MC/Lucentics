package io.github.rontyamc.lucentics.common.beam;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record BeamNode(ResourceLocation blockId, BlockPos pos, ResourceKey<Level> dimension) {
    public static final Codec<BeamNode> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(BeamNode::blockId),
            BlockPos.CODEC.fieldOf("pos").forGetter(BeamNode::pos),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(BeamNode::dimension)
    ).apply(ins, BeamNode::new));
}
