package io.github.rontyamc.lucentics.common.info;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

public enum Colors implements StringRepresentable {
    SUNLIGHT("sunlight", 1, 0xFFF9C4, null),

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
}
