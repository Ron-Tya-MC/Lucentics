package io.github.rontyamc.lucentics.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MiscFuncs {
    private MiscFuncs() {}

    public static JsonObject loadJson(String resourcePath) {
        try (InputStream stream = MiscFuncs.class.getResourceAsStream(resourcePath)) {
            if (stream == null) throw new IllegalStateException("Missing json file: " + resourcePath);
            return new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static Vec3 getCenter(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }
}
