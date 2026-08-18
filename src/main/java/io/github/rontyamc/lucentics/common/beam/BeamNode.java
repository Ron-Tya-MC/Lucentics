package io.github.rontyamc.lucentics.common.beam;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record BeamNode(ResourceLocation blockId, BlockPos pos, ResourceKey<Level> dimension) {
}
