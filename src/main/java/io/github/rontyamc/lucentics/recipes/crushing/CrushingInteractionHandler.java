package io.github.rontyamc.lucentics.recipes.crushing;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.recipe.OutputRoller;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Optional;

@EventBusSubscriber(modid = Lucentics.MOD_ID)
public class CrushingInteractionHandler {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        BlockPos pos = event.getPos();
        BlockState state = serverLevel.getBlockState(pos);
        ItemStack tool = event.getItemStack();

        CrushingRecipeInput input = new CrushingRecipeInput(state, tool);
        Optional<RecipeHolder<CrushingRecipe>> recipeHolder = serverLevel.getRecipeManager()
                .getRecipeFor(LucenticsRecipeTypesRegister.CRUSHING_TYPE.get(), input, serverLevel);

        if (recipeHolder.isEmpty()) return;

        event.setCanceled(true);
        serverPlayer.swing(event.getHand(), true);

        RecipeHolder<CrushingRecipe> holder = recipeHolder.get();
        CrushingRecipe recipe = holder.value();
        ResourceLocation recipeId = holder.id();
        var args = recipe.getArguments();

        LevelChunk chunk = serverLevel.getChunkAt(pos);
        CrushingProgressEntry existing = CrushingProgressHelper.get(chunk, pos);
        int hits = (existing != null && existing.recipeId().equals(recipeId)) ? existing.hits() : 0;
        hits++;

        boolean reserveBreak = hits >= args.requiredHits();

        if (reserveBreak) {
            BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(serverLevel, pos, state, serverPlayer);
            NeoForge.EVENT_BUS.post(breakEvent);
            if (breakEvent.isCanceled()) {
                return;
            }
        }

        args.clickSound().ifPresent(spec ->
                serverLevel.playSound(null, pos, spec.sound().value(), spec.source(), spec.volume(), spec.pitch()));

        if (args.damagePerHit() > 0 && !tool.isEmpty()) {
            tool.hurtAndBreak(args.damagePerHit(), serverLevel, serverPlayer, item -> {});
        }

        if (reserveBreak) {
            args.breakSound().ifPresent(spec ->
                    serverLevel.playSound(null, pos, spec.sound().value(), spec.source(), spec.volume(), spec.pitch()));

            serverLevel.destroyBlock(pos, false, serverPlayer);

            for (OutputRoller.RolledOutput rolled : OutputRoller.roll(serverLevel.getRandom(), recipe.getOutputs())) {
                rolled.item().ifPresent(item -> ItemUtil.dropItem(serverLevel, pos, item));
            }
        } else {
            int progress = Mth.clamp((int) ((float) hits / args.requiredHits() * 9), 0, 9);
            serverLevel.destroyBlockProgress(pos.hashCode(), pos, progress);
            CrushingProgressHelper.set(chunk, pos, recipeId, hits);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAnyBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        BlockPos pos = event.getPos();
        LevelChunk chunk = serverLevel.getChunkAt(pos);
        if (CrushingProgressHelper.get(chunk, pos) != null) {
            serverLevel.destroyBlockProgress(pos.hashCode(), pos, -1);
            CrushingProgressHelper.clear(chunk, pos);
        }
    }
}
