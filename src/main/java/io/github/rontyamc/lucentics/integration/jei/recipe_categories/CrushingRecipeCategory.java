package io.github.rontyamc.lucentics.integration.jei.recipe_categories;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.integration.jei.LucenticsJEIIntegration;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingRecipe;
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
import net.minecraft.world.item.ItemStack;

import java.util.List;

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
    public void draw(CrushingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, WIDTH / 2 - 30, 24);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrushingRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> stacks = recipe.getArguments().block().stream()
                .map(holder -> new ItemStack(holder.value().asItem()))
                .toList();
        builder.addInputSlot(10, 25)
                .setStandardSlotBackground()
                .addItemStacks(stacks);

        builder.addInputSlot(WIDTH / 2 - 7, 5)
                .setStandardSlotBackground()
                .addIngredients(recipe.getArguments().tool());

        builder.addOutputSlot(WIDTH - 27, 25)
                .setStandardSlotBackground()
                .addItemStack(RecipeUtil.getResultItem(recipe));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, CrushingRecipe recipe, IFocusGroup focuses) {
        addRequiredHit(builder, recipe);
        addDamagePerHits(builder, recipe);
    }

    protected void addRequiredHit(IRecipeExtrasBuilder builder, CrushingRecipe recipe) {
        int requiredHits = recipe.getArguments().requiredHits();
        if (requiredHits <= 1) {
            requiredHits = 1;
        }

        Component text = Component.translatable("jei.lucentics.info.required_hits", requiredHits);
        builder.addText(text, getWidth() / 2, 10)
                .setPosition(5, 0, getWidth(), getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM)
                .setTextAlignment(HorizontalAlignment.LEFT)
                .setColor(0xFF808080);
    }

    protected void addDamagePerHits(IRecipeExtrasBuilder builder, CrushingRecipe recipe) {
        int damagePerHit = recipe.getArguments().damagePerHit();
        if (damagePerHit < 1) {
            return;
        }

        Component text = Component.translatable("jei.lucentics.info.damage_per_hits", damagePerHit);
        builder.addText(text, getWidth() / 2, 10)
                .setPosition(-5, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
                .setTextAlignment(HorizontalAlignment.RIGHT)
                .setColor(0xFF808080);
    }
}
