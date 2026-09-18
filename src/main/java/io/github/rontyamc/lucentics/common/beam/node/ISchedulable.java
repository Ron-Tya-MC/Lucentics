package io.github.rontyamc.lucentics.common.beam.node;

import io.github.rontyamc.lucentics.common.behavior.IScheduleTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

// ブロックにつける
public interface ISchedulable {
    void registerSchedule(ServerLevel level, BlockPos pos, ResourceLocation id, int timer);

    IScheduleTicker getTicker();
}
