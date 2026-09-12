package io.github.rontyamc.lucentics.integration.jei.recipe_categories;

import io.github.rontyamc.lucentics.integration.jei.CommonParts;
import io.github.rontyamc.lucentics.integration.jei.LucenticsJEIIntegration;
import io.github.rontyamc.lucentics.integration.jei.ProbabilisticOutputSlots;
import io.github.rontyamc.lucentics.recipes.injecting.InjectingRecipe;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class InjectingRecipeCategory extends AbstractRecipeCategory<InjectingRecipe> {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;

    public InjectingRecipeCategory(IGuiHelper guiHelper) {
        super(
                LucenticsJEIIntegration.INJECTING,
                Component.translatable("jei.lucentics.category.injector"),
                guiHelper.createDrawableItemStack(LucenticsBlockRegister.INJECTOR.asStack()),
                WIDTH,
                HEIGHT
        );
    }

    @Override
    public void draw(InjectingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        CommonParts.arrowLight.draw(guiGraphics, WIDTH / 2 - 24, 4);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InjectingRecipe recipe, IFocusGroup focuses) {
        recipe.getMainInput().asItem().ifPresent(sized ->
                builder.addInputSlot(10, 5)
                        .setBackground(CommonParts.slot_normal, -1, -1)
                        .addIngredients(sized.ingredient())
        );

        ProbabilisticOutputSlots.addSlots(builder, recipe.getOutputs(), 93, 5, 20, 1);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, InjectingRecipe recipe, IFocusGroup focuses) {
        addDaylightCondition(builder, recipe);
        addProcessingDuration(builder, recipe);
    }

    protected void addDaylightCondition(IRecipeExtrasBuilder builder, InjectingRecipe recipe) {
        int daylightCondition = recipe.getDayLightCondition();
        if (daylightCondition <= 0) {
            daylightCondition = 0;
        }

        Component text = Component.translatable("jei.lucentics.info.daylight_condition", daylightCondition);
        builder.addText(text, getWidth() / 2, 10)
                    .setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM)
                    .setTextAlignment(HorizontalAlignment.LEFT)
                    .setColor(0xFF808080);
    }

    protected void addProcessingDuration(IRecipeExtrasBuilder builder, InjectingRecipe recipe) {
        int processingDuration = recipe.getProcessingDuration();
        if (processingDuration <= 0) {
            processingDuration = 0;
        }

        Component text = Component.translatable("jei.lucentics.info.processing_duration", LucenticsJEIIntegration.makeSecond(processingDuration));
        builder.addText(text, getWidth() / 2, 10)
                .setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
                .setTextAlignment(HorizontalAlignment.RIGHT)
                .setColor(0xFF808080);
    }
}