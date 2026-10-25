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

public class AssemblingRecipeCategory extends TrailRecipeCategory {
    public AssemblingRecipeCategory(IGuiHelper guiHelper) {
        super(
                LucenticsJEIIntegration.ASSEMBLING,
                Component.translatable("jei.lucentics.category.assembling"),
                guiHelper.createDrawableItemStack(LucenticsBlockRegister.ASSEMBLING_TABLE.asStack()),
                guiHelper
        );
    }

    @Override
    public void draw(TrailRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (recipe.getDayLightCondition() > 0) drawArrowLight(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        else drawArrowNormal(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, TrailRecipe recipe, IFocusGroup focuses) {
        addProcessingDuration(builder, recipe);
        if (recipe.getDayLightCondition() > 0) addDaylightCondition(builder, recipe);
    }
}
