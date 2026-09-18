package io.github.rontyamc.lucentics.recipes.dyeing.process;

import io.github.rontyamc.lucentics.registers.LucenticsAttachmentRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;

public class DyeingProgressHelper {
    public static DyeingProgressEntry get(LevelChunk chunk, BlockPos pos) {
        return chunk.getData(LucenticsAttachmentRegister.DYEING_PROGRESS).get(pos.immutable());
    }

    public static void set(LevelChunk chunk, BlockPos pos, ResourceLocation itemId, String color, int processingTime) {
        chunk.getData(LucenticsAttachmentRegister.DYEING_PROGRESS)
                .put(pos.immutable(), new DyeingProgressEntry(pos.immutable(), itemId, color, processingTime));
    }

    public static void clear(LevelChunk chunk, BlockPos pos) {
        chunk.getData(LucenticsAttachmentRegister.DYEING_PROGRESS).remove(pos.immutable());
    }
}
