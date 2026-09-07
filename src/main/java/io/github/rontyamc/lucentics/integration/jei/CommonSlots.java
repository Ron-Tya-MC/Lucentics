package io.github.rontyamc.lucentics.integration.jei;

import io.github.rontyamc.lucentics.Lucentics;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.resources.ResourceLocation;

public class CommonSlots {
    private static final ResourceLocation SLOT_NORMAL_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/slot_normal.png");
    private static final ResourceLocation SLOT_FRAMED_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/slot_framed.png");
    private static final ResourceLocation SLOT_BLUE_BLUR_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/slot_blue_blur.png");
    private static final ResourceLocation SLOT_YELLOW_BLUR_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/slot_yellow_blur.png");

    public static IDrawableStatic slot_normal;
    public static IDrawableStatic slot_framed;
    public static IDrawableStatic slot_blue_blur;
    public static IDrawableStatic slot_yellow_blur;

    private CommonSlots() {}

    protected static void buildCommonSlots(IGuiHelper guiHelper) {
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
    }
}
