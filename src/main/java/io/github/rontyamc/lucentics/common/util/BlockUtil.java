package io.github.rontyamc.lucentics.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.List;

public final class BlockUtil {
    public static boolean isIdInTag(ResourceLocation id, TagKey<Block> tagKey, Level level) {
        return level.registryAccess().lookup(Registries.BLOCK)
                .flatMap(registry -> registry.get(ResourceKey.create(Registries.BLOCK, id)))
                .map(holder -> holder.is(tagKey))
                .orElse(false);
    }

    public static List<Block> blocksInBlockTag(TagKey<Block> tagKey, Level level) {
        return level.registryAccess().lookup(Registries.BLOCK)
                .flatMap(registry -> registry.get(tagKey))
                .map(holderSet -> holderSet.stream()
                        .map(Holder::value)
                        .toList()
                )
                .orElse(List.of());
    }

    public static List<Block> blocksInItemTag(TagKey<Item> tagKey, Level level) {
        return level.registryAccess().lookup(Registries.ITEM)
                .flatMap(registry -> registry.get(tagKey))
                .map(holderSet -> holderSet.stream()
                        .filter(item -> ItemUtil.isItemBlock(item.value()))
                        .map(item -> Block.byItem(item.value()))
                        .toList()
                )
                .orElse(List.of());
    }

    public static boolean canPlaceBlock(BlockItem blockItem, BlockPlaceContext context) {
        if (!context.canPlace()) return false;

        BlockState state = blockItem.getBlock().getStateForPlacement(context);
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();

        if (state == null) return false;
        if (!state.canSurvive(level, blockPos)) return false;
        if (!level.isUnobstructed(state, blockPos, CollisionContext.empty())) return false;

        return true;
    }
}
