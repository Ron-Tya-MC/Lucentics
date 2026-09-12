package io.github.rontyamc.lucentics.integration.jei;

import io.github.rontyamc.lucentics.Lucentics;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.resources.ResourceLocation;

public class CommonParts {
    private static final ResourceLocation SLOT_NORMAL_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/slot_normal.png");
    private static final ResourceLocation SLOT_FRAMED_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/slot_framed.png");
    private static final ResourceLocation SLOT_BLUE_BLUR_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/slot_blue_blur.png");
    private static final ResourceLocation SLOT_YELLOW_BLUR_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/slot_yellow_blur.png");

    private static final ResourceLocation ARROW_LIGHT_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/injector_arrow.png");
    private static final ResourceLocation ARROW_NORMAL_60_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/normal_arrow_60.png");
    private static final ResourceLocation ARROW_NORMAL_48_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/normal_arrow_48.png");

    public static IDrawableStatic slot_normal;
    public static IDrawableStatic slot_framed;
    public static IDrawableStatic slot_blue_blur;
    public static IDrawableStatic slot_yellow_blur;

    public static IDrawableStatic arrowLight;
    public static IDrawableStatic arrowNormal60;
    public static IDrawableStatic arrowNormal48;

    private CommonParts() {}

    protected static void buildCommonParts(IGuiHelper guiHelper) {
        slot_normal = guiHelper.drawableBuilder(SLOT_NORMAL_TEXTURE, 0, 0, 18, 18)
                .setTextureSize(18, 18)
                .build();

        slot_framed = guiHelper.drawableBuilder(SLOT_FRAMED_TEXTURE, 0, 0, 18, 18)
                .setTextureSize(18, 18)
                .build();

        slot_blue_blur = guiHelper.drawableBuilder(SLOT_BLUE_BLUR_TEXTURE, 0, 0, 18, 18)
                .setTextureSize(18, 18)
                .build();

        slot_yellow_blur = guiHelper.drawableBuilder(SLOT_YELLOW_BLUR_TEXTURE, 0, 0, 18, 18)
                .setTextureSize(18, 18)
                .build();

        arrowLight = guiHelper.drawableBuilder(ARROW_LIGHT_TEXTURE, 0, 0, 48, 18)
                .setTextureSize(48, 18)
                .build();

        arrowNormal60 = guiHelper.drawableBuilder(ARROW_NORMAL_60_TEXTURE, 0, 0, 60, 20)
                .setTextureSize(60, 20)
                .build();

        arrowNormal48 = guiHelper.drawableBuilder(ARROW_NORMAL_48_TEXTURE, 0, 0, 48, 18)
                .setTextureSize(48, 18)
                .build();
    }
}
