package io.github.rontyamc.lucentics.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.github.rontyamc.lucentics.Lucentics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class MiscUtil {
    private MiscUtil() {}

    public static JsonObject loadJson(String resourcePath) {
        try (InputStream stream = MiscUtil.class.getResourceAsStream(resourcePath)) {
            if (stream == null) throw new IllegalStateException("Missing json file: " + resourcePath);
            return new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static <T> Codec<List<T>> singleOrList(Codec<T> codec) {
        return Codec.either(codec, codec.listOf()).xmap(
                either -> either.map(List::of, java.util.function.Function.identity()),
                list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list)
        );
    }

    public static double ceilSignificantDigits(double d, int sigDigits) {
        if (d == 0.0) return 0.0;
        int scale = sigDigits - (int) Math.floor(Math.log10(Math.abs(d))) - 1;
        BigDecimal bd = BigDecimal.valueOf(d);
        bd = bd.setScale(scale, RoundingMode.HALF_DOWN);
        return bd.doubleValue();
    }

    public static double shapePercentage(double percentage) {
        if (percentage >= 100.0) return 100;
        if (percentage >= 99.0) return 99;
        if (percentage >= 10.0) return Math.round(percentage);
        return ceilSignificantDigits(percentage, 2);
    }

    public static void perSecondInfo(Level level, String format, Object... arguments) {
        if (level.getGameTime()%20 == 0) Lucentics.LOGGER.info(format, arguments);
    }

    public static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split("[_-]");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
                if (i < words.length - 1) result.append(" ");
            }
        }
        return result.toString();
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, Vec3::x,
            ByteBufCodecs.DOUBLE, Vec3::y,
            ByteBufCodecs.DOUBLE, Vec3::z,
            Vec3::new
    );

    public static <T> ResourceLocation getKeyOrThrow(T object) {
        if (object instanceof ResourceLocation) {
            return (ResourceLocation) object;
        }

        ResourceLocation key = null;

        if (object instanceof Block block) {
            key = BuiltInRegistries.BLOCK.getKey(block);
        }
        if (object instanceof Item item) {
            key = BuiltInRegistries.ITEM.getKey(item);
        }
        if (object instanceof Fluid fluid) {
            key = BuiltInRegistries.FLUID.getKey(fluid);
        }
        if (object instanceof RecipeSerializer<?> serializer) {
            key = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
        }

        if (key == null) {
            throw new IllegalStateException("Unregistered object: " + object + " has no registry key!");
        }

        return key;
    }
}
