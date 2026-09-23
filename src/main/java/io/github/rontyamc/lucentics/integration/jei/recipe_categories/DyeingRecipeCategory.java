package io.github.rontyamc.lucentics.integration.jei.recipe_categories;

import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.util.GUIUtil;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import io.github.rontyamc.lucentics.integration.jei.CommonParts;
import io.github.rontyamc.lucentics.integration.jei.LucenticsJEIIntegration;
import io.github.rontyamc.lucentics.recipes.dyeing.DyeingRecipeEntry;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsFluidRegister;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Collections;
import java.util.List;

public class DyeingRecipeCategory extends AbstractRecipeCategory<DyeingRecipeEntry> {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;

    public DyeingRecipeCategory(IGuiHelper guiHelper) {
        super(
                LucenticsJEIIntegration.DYEING,
                Component.translatable("jei.lucentics.category.dyeing"),
                guiHelper.createDrawableItemStack(LucenticsBlockRegister.MIXING_TABLE.asStack()),
                WIDTH,
                HEIGHT
        );
    }

    @Override
    public void draw(DyeingRecipeEntry recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        CommonParts.arrowWaveLeft21.draw(guiGraphics, WIDTH / 2 - 30, 0);

        Colors color = recipe.color();
        GUIUtil.drawColored(guiGraphics, CommonParts.arrowWhiteRight21, WIDTH / 2 + 9, 0, color);
        CommonParts.arrowShadowRight21.draw(guiGraphics, WIDTH / 2 + 9, 0);

        CommonParts.underCover.draw(guiGraphics, 8, 17);
        CommonParts.underCover.draw(guiGraphics, 50, 17);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DyeingRecipeEntry recipe, IFocusGroup focuses) {
        builder.addInputSlot(10, 1)
                .setBackground(CommonParts.slot_normal, -1, -1)
                .addIngredients(recipe.ingredient());

        FluidStack fluid = new FluidStack(LucenticsFluidRegister.DYE_LIQUIDS.get(recipe.color()).get().getSource(), recipe.liquidAmount());

        builder.addInputSlot(52, 1)
                .setBackground(CommonParts.slot_framed, -1, -1)
                .setFluidRenderer(1, false, 16, 16)
                .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(fluid));

        builder.addOutputSlot(94, 1)
                .setBackground(CommonParts.slot_normal, -1, -1)
                .addItemStack(recipe.result());

        var level = Minecraft.getInstance().level;

        List<Item> pedestals;
        List<ItemStack> pedestalStacks;

        if (level != null) {
            pedestals = ItemUtil.itemsInBlockTag(LucenticsTagRegister.LucenticsBTags.PEDESTAL_RITUAL.tag, level);
            pedestalStacks = pedestals.stream().map(ItemStack::new).toList();
        }
        else pedestalStacks = Collections.emptyList();

        if (!pedestalStacks.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 10, 17)
                    .addItemStacks(pedestalStacks);
        }

        builder.addSlot(RecipeIngredientRole.CATALYST, 52, 17)
                .addItemStack(new ItemStack(LucenticsBlockRegister.MIXING_TABLE));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, DyeingRecipeEntry recipe, IFocusGroup focuses) {
        addProcessingDuration(builder, recipe);
    }

    protected void addProcessingDuration(IRecipeExtrasBuilder builder, DyeingRecipeEntry recipe) {
        int processingDuration = recipe.processingDuration();
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