package io.github.rontyamc.lucentics.common.beam.node;

import io.github.rontyamc.lucentics.registers.LucenticsAttachmentRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashMap;
import java.util.Map;

// プリズムがエンティティを持たないゆえの苦肉の策
public class NodeScheduleHelper {
    public static NodeScheduleEntry get(LevelChunk chunk, BlockPos pos) {
        NodeScheduleEntry entry = chunk.getData(LucenticsAttachmentRegister.NODE_SCHEDULE).get(pos.immutable());
        if (entry == null || entry.scheduleMap() == null) {
            set(chunk, pos, new HashMap<>());
        }
        return chunk.getData(LucenticsAttachmentRegister.NODE_SCHEDULE).get(pos.immutable());
    }

    public static void set(LevelChunk chunk, BlockPos pos, Map<ResourceLocation, Long> scheduleMap) {
        chunk.getData(LucenticsAttachmentRegister.NODE_SCHEDULE)
                .put(pos.immutable(), new NodeScheduleEntry(scheduleMap, pos.immutable()));
    }

    public static long getTimer(LevelChunk chunk, BlockPos pos, ResourceLocation scheduleId) {
        if (isSchedulePresent(chunk, pos, scheduleId)) return get(chunk, pos.immutable()).scheduleMap().get(scheduleId);
        return -1;
    }

    public static boolean isSchedulePresent(LevelChunk chunk, BlockPos pos, ResourceLocation scheduleId) {
        if (chunk.getData(LucenticsAttachmentRegister.NODE_SCHEDULE).get(pos.immutable()) == null) return false;
        return get(chunk, pos.immutable()).scheduleMap().containsKey(scheduleId);
    }

    public static void append(LevelChunk chunk, BlockPos pos, ResourceLocation scheduleId, long timer, boolean force) {
        if (timer <= 0) return;
        if (!isSchedulePresent(chunk, pos.immutable(), scheduleId) || force)
            get(chunk, pos.immutable()).scheduleMap().put(scheduleId, timer);
    }

    public static void extend(LevelChunk chunk, BlockPos pos, ResourceLocation scheduleId, long extend) {
        if (isSchedulePresent(chunk, pos.immutable(), scheduleId)) {
            long timer = getTimer(chunk, pos.immutable(), scheduleId) + extend;
            if (timer > 0) get(chunk, pos.immutable()).scheduleMap().put(scheduleId, timer);
            else get(chunk, pos.immutable()).scheduleMap().remove(scheduleId);
        }
    }

    public static long tick(LevelChunk chunk, BlockPos pos, ResourceLocation scheduleId) {
        long ticked = -1;
        if (isSchedulePresent(chunk, pos.immutable(), scheduleId)) {
            ticked = getTimer(chunk, pos.immutable(), scheduleId) - 1;
            if (ticked > 0) get(chunk, pos.immutable()).scheduleMap().put(scheduleId, ticked);
            else get(chunk, pos.immutable()).scheduleMap().remove(scheduleId);
        }
        return ticked;
    }

    public static void remove(LevelChunk chunk, BlockPos pos, ResourceLocation scheduleId) {
        if (isSchedulePresent(chunk, pos.immutable(), scheduleId)) {
            get(chunk, pos.immutable()).scheduleMap().remove(scheduleId);
        }
    }

    public static void clear(LevelChunk chunk, BlockPos pos) {
        chunk.getData(LucenticsAttachmentRegister.NODE_SCHEDULE).remove(pos.immutable());
    }
}
