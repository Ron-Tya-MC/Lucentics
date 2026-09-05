package io.github.rontyamc.lucentics.recipes.crushing;

import io.github.rontyamc.lucentics.registers.LucenticsAttachmentRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;

public class CrushingProgressHelper {
    public static CrushingProgressEntry get(LevelChunk chunk, BlockPos pos) {
        return chunk.getData(LucenticsAttachmentRegister.CRUSHING_PROGRESS).get(pos.immutable());
    }

    public static void set(LevelChunk chunk, BlockPos pos, ResourceLocation recipeId, int hits) {
        chunk.getData(LucenticsAttachmentRegister.CRUSHING_PROGRESS)
                .put(pos.immutable(), new CrushingProgressEntry(pos.immutable(), recipeId, hits));
    }

    public static void clear(LevelChunk chunk, BlockPos pos) {
        chunk.getData(LucenticsAttachmentRegister.CRUSHING_PROGRESS).remove(pos.immutable());
    }
}
