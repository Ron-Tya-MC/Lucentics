package io.github.rontyamc.lucentics.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MiscUtilities {
    private MiscUtilities() {}

    public static JsonObject loadJson(String resourcePath) {
        try (InputStream stream = MiscUtilities.class.getResourceAsStream(resourcePath)) {
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
}
