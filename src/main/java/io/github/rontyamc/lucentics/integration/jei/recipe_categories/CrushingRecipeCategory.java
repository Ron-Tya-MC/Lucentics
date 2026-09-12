package io.github.rontyamc.lucentics.integration.jei.recipe_categories;

import io.github.rontyamc.lucentics.integration.jei.CommonParts;
import io.github.rontyamc.lucentics.integration.jei.LucenticsJEIIntegration;
import io.github.rontyamc.lucentics.integration.jei.ProbabilisticOutputSlots;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingRecipe;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
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
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CrushingRecipeCategory extends AbstractRecipeCategory<CrushingRecipe> {
    private static final int WIDTH = 160;
    private static final int HEIGHT = 60;

    private static final int LEFT_SPACE = 20;

    public CrushingRecipeCategory(IGuiHelper guiHelper) {
        super(
                LucenticsJEIIntegration.CRUSHING,
                Component.translatable("jei.lucentics.category.crushing"),
                guiHelper.createDrawableItemStack(LucenticsItemRegister.COPPER_HAMMER.asStack()),
                WIDTH,
                HEIGHT
        );
    }

    @Override
    public void draw(CrushingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int left = recipe.getOutputs().size() >= 2 ? LEFT_SPACE - 10 : LEFT_SPACE;

        CommonParts.arrowNormal60.draw(guiGraphics, left + 30, 24);
        ProbabilisticOutputSlots.drawRangeBadges(guiGraphics, recipeSlotsView, recipe.getOutputs(), left + 102, 25, 20, 4);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrushingRecipe recipe, IFocusGroup focuses) {
        int left = recipe.getOutputs().size() >= 2 ? LEFT_SPACE - 10 : LEFT_SPACE;

        List<ItemStack> stacks = recipe.getArguments().block().stream()
                .map(holder -> new ItemStack(holder.value().asItem()))
                .toList();
        builder.addInputSlot(left, 25)
                .setBackground(CommonParts.slot_normal, -1, -1)
                .addItemStacks(stacks);

        builder.addInputSlot(left + 51, 10)
                .setBackground(CommonParts.slot_framed, -1, -1)
                .addIngredients(recipe.getArguments().tool());

        ProbabilisticOutputSlots.addSlots(builder, recipe.getOutputs(), left + 102, 25, 20, 4);
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
