package io.github.rontyamc.lucentics.common.dict;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public enum Colors implements StringRepresentable {
    SUNLIGHT("sunlight", 1, 16775620, null),

    RED(DyeColor.RED, 2),
    BLUE(DyeColor.BLUE, 2),
    GREEN(DyeColor.GREEN, 2),
    YELLOW(DyeColor.YELLOW, 3),
    MAGENTA(DyeColor.MAGENTA, 3),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE, 3),
    PINK(DyeColor.PINK, 4),
    CYAN(DyeColor.CYAN, 4),
    LIME(DyeColor.LIME, 4),
    GRAY(DyeColor.GRAY, 5),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY, 5),
    BROWN(DyeColor.BROWN, 5),
    PURPLE(DyeColor.PURPLE, 6),
    ORANGE(DyeColor.ORANGE, 6),
    WHITE(DyeColor.WHITE, 7),
    BLACK(DyeColor.BLACK, 7);

    private final String name;
    private final int tier;
    private final int colorCode;
    private final @Nullable DyeColor dyeColor;

    Colors(DyeColor dyeColor, int tier) {
        this(dyeColor.getName(), tier, dyeColor.getTextureDiffuseColor(), dyeColor);
    }

    Colors(String name, int tier, int colorCode, @Nullable DyeColor dyeColor) {
        this.name = name;
        this.tier = tier;
        this.colorCode = colorCode;
        this.dyeColor = dyeColor;
    }

    public static final Codec<Colors> CODEC = Codec.STRING.comapFlatMap(
            name -> Colors.byName(name)
                    .map(com.mojang.serialization.DataResult::success)
                    .orElseGet(() -> com.mojang.serialization.DataResult.error(() -> "Unknown color: " + name)),
            Colors::getSerializedName
    );

    public static final StreamCodec<ByteBuf,Colors> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(
            name -> Colors.byName(name).orElse(Colors.SUNLIGHT), Colors::getSerializedName
    );

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public int getTier() {
        return tier;
    }

    public int getColorCode() {
        return colorCode;
    }

    public @Nullable DyeColor getDyeColor() {
        return dyeColor;
    }

    public static Optional<Colors> byName(String name) {
        for (Colors color : Colors.values()) {
            if (color.getName().equals(name)) {
                return Optional.of(color);
            }
        }

        return Optional.empty();
    }

    public static Colors[] getAllColors() {
        return Colors.values();
    }

    public static ArrayList<Colors> get16Colors() {
        ArrayList<Colors> colors = new ArrayList<>();

        for (Colors color : Colors.values()) {
            if (!Objects.equals(color.getName(), "sunlight")) colors.add(color);
        }

        return colors;
    }

    public static ArrayList<Colors> byTier(int tier) {
        ArrayList<Colors> colors = new ArrayList<>();

        for (Colors color : Colors.values()) {
            if (color.getTier() == tier) {
                colors.add(color);
            }
        }

        return colors;
    }

    public static TagKey<Item> cTag(Colors color) {
        if (color.equals(Colors.SUNLIGHT)) return null; // ありません
        return TagKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("c", "dyes/" + color.getName()));
    }

    public static Colors byDyeColor(DyeColor dyeColor) {
        if (dyeColor == null) return null;
        for (Colors color : Colors.values()) {
            if (color.getDyeColor() == dyeColor) return color;
        }
        return null;
    }
}
