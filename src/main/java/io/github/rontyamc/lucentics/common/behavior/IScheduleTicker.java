package io.github.rontyamc.lucentics.common.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;


// behaviorにつける
public interface IScheduleTicker {
    void tickSchedule(ServerLevel level, BlockPos prismPos);
}
