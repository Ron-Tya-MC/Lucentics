package io.github.rontyamc.lucentics.integration.jei.recipe_categories;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.recipes.injecting.InjectingRecipe;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingRecipe;
import io.github.rontyamc.lucentics.integration.jei.LucenticsJEIIntegration;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CrushingRecipeCategory extends AbstractRecipeCategory<CrushingRecipe> {
    private static final int WIDTH = 140;
    private static final int HEIGHT = 60;

    private static final ResourceLocation ARROW_TEXTURE = Lucentics.defaultLocation("textures/gui/jei/normal_arrow_60.png");

    private final IDrawableStatic arrow;

    public CrushingRecipeCategory(IGuiHelper guiHelper) {
        super(
                LucenticsJEIIntegration.CRUSHING,
                Component.translatable("jei.lucentics.category.crushing"),
                guiHelper.createDrawableItemStack(LucenticsItemRegister.COPPER_HAMMER.asStack()),
                WIDTH,
                HEIGHT
        );

        this.arrow = guiHelper.drawableBuilder(ARROW_TEXTURE, 0, 0, 60, 20)
                .setTextureSize(60, 20)
                .build();
    }

    @Override
    public void draw(InjectingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, WIDTH / 2 - 30, 24);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrushingRecipe recipe, IFocusGroup focuses) {
        recipe.getMainInput().ifPresent(sized ->
                builder.addInputSlot(10, 25)
                        .setStandardSlotBackground()
                        .addIngredients(sized.ingredient())
        );

        builder.addOutputSlot(WIDTH - 27, 25)
                .setStandardSlotBackground()
                .addItemStack(RecipeUtil.getResultItem(recipe));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, CrushingRecipe recipe, IFocusGroup focuses) {
        addRequiredHit(builder, recipe);
    }

    protected void addRequiredHit(IRecipeExtrasBuilder builder, CrushingRecipe recipe) {
        int requiredHit = recipe.getRequiredHit();
        if (requiredHit <= 1) {
            requiredHit = 1;
        }

        Component text = Component.translatable("jei.lucentics.info.required_hit", requiredHit);
        builder.addText(text, getWidth() - 20, 10)
                .setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM)
                .setTextAlignment(HorizontalAlignment.CENTER)
                .setColor(0xFF808080);
    }
}
