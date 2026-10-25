package io.github.rontyamc.lucentics.common.util;

import io.github.rontyamc.lucentics.Config;
import io.github.rontyamc.lucentics.common.advancement.AdvancementEventType;
import io.github.rontyamc.lucentics.registers.LucenticsTriggerRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class AdvancementUtil {
    private AdvancementUtil() {}

    public static void grant(ServerPlayer player, AdvancementEventType event, ResourceLocation recipeId) {
        LucenticsTriggerRegister.EVENT.get().trigger(player, event, recipeId);
    }

    public static void grant(ServerPlayer player, AdvancementEventType event) {
        grant(player, event, null);
    }

    public static void grant(ServerPlayer player, Supplier<? extends AdvancementEventType> event) {
        grant(player, event.get(), null);
    }

    public static void grant(ServerPlayer player, Supplier<? extends AdvancementEventType> event, ResourceLocation recipeId) {
        grant(player, event.get(), recipeId);
    }

    private static void grantNearby(ServerLevel level, BlockPos pos, AdvancementEventType event,
                                            @Nullable ResourceLocation recipeId, double radius) {
        Vec3 center = Vec3.atCenterOf(pos);
        double radiusSqr = radius * radius;

        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(center) <= radiusSqr) grant(player, event, recipeId);
        }
    }

    public static void grantNearby(ServerLevel level, BlockPos pos, AdvancementEventType event) {
        grantNearby(level, pos, event, null, Config.ADVANCEMENT_GRANT_RADIUS.get());
    }

    public static void grantNearby(ServerLevel level, BlockPos pos, Supplier<? extends AdvancementEventType> event) {
        grantNearby(level, pos, event.get(), null, Config.ADVANCEMENT_GRANT_RADIUS.get());
    }

    public static void grantNearby(ServerLevel level, BlockPos pos, AdvancementEventType event, ResourceLocation recipeId) {
        grantNearby(level, pos, event, recipeId, Config.ADVANCEMENT_GRANT_RADIUS.get());
    }

    public static void grantNearby(ServerLevel level, BlockPos pos, Supplier<? extends AdvancementEventType> event, ResourceLocation recipeId) {
        grantNearby(level, pos, event.get(), recipeId, Config.ADVANCEMENT_GRANT_RADIUS.get());
    }

    public static void grantNearby(ServerLevel level, BlockPos pos, AdvancementEventType event, double radius) {
        grantNearby(level, pos, event, null, radius);
    }

    public static void grantNearby(ServerLevel level, BlockPos pos, Supplier<? extends AdvancementEventType> event, double radius) {
        grantNearby(level, pos, event.get(), null, radius);
    }

    public static void grantNearby(ServerLevel level, BlockPos pos, Supplier<? extends AdvancementEventType> event, ResourceLocation recipeId, double radius) {
        grantNearby(level, pos, event.get(), recipeId, radius);
    }
}
