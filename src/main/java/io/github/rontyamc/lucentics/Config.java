package io.github.rontyamc.lucentics;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue ADVANCEMENT_GRANT_RADIUS = BUILDER
            .comment("進捗の付与範囲")
            .defineInRange("advancement_grant_radius", 12, 1, 64);

    static final ModConfigSpec SPEC = BUILDER.build();
}
