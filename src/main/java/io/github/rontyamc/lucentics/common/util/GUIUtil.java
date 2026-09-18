package io.github.rontyamc.lucentics.common.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.rontyamc.lucentics.common.dict.Colors;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import net.minecraft.client.gui.GuiGraphics;

public class GUIUtil {
    public static void drawColored(GuiGraphics guiGraphics, IDrawableStatic drawable, int x, int y, Colors color) {
        int rgb = color.getColorCode();
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(r, g, b, 1.0f);
        drawable.draw(guiGraphics, x, y);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
