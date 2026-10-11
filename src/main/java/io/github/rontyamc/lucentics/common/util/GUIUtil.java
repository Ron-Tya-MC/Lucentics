package io.github.rontyamc.lucentics.common.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.rontyamc.lucentics.common.dict.Colors;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import net.minecraft.client.gui.GuiGraphics;

public class GUIUtil {
    public static void drawColored(GuiGraphics guiGraphics, IDrawableStatic drawable, int x, int y, Colors color) {
        int rgbInt = color.getColorCode();
        Float[] rgb = MiscUtil.separateRGB(rgbInt);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(rgb[0], rgb[1], rgb[2], 1.0f);
        drawable.draw(guiGraphics, x, y);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
