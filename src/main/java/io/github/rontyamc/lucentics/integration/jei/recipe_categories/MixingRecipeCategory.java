package io.github.rontyamc.lucentics.integration.jei.recipe_categories;

import io.github.rontyamc.lucentics.integration.jei.LucenticsJEIIntegration;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class MixingRecipeCategory extends TrailRecipeCategory {
    public MixingRecipeCategory(IGuiHelper guiHelper) {
        super(
                LucenticsJEIIntegration.MIXING,
                Component.translatable("jei.lucentics.category.mixing"),
                guiHelper.createDrawableItemStack(LucenticsBlockRegister.MIXING_TABLE.asStack()),
                guiHelper
        );
    }

    @Override
    public void draw(TrailRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        drawArrowNormal(guiGraphics);
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, TrailRecipe recipe, IFocusGroup focuses) {
        addProcessingDuration(builder, recipe);
    }
}
