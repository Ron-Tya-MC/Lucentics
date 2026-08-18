package io.github.rontyamc.lucentics.integration.jei.recipe_categories;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRecipe;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class InjectingRecipeCategory implements IRecipeCategory<InjectorRecipe> {
    public static final RecipeType<InjectorRecipe> TYPE =
            RecipeType.create(Lucentics.MOD_ID, "injector", InjectorRecipe.class);

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;

    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Lucentics.MOD_ID, "textures/gui/jei/injector_arrow.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic slot;
    private final IDrawableStatic arrow;
    private final Font font = Minecraft.getInstance().font;

    public InjectingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(LucenticsBlockRegister.INJECTOR.asStack());
        this.slot = guiHelper.getSlotDrawable();

        this.arrow = guiHelper.drawableBuilder(ARROW_TEXTURE, 0, 0, 48, 18)
                .setTextureSize(48, 18)
                .build();
    }

    @Override
    public RecipeType<InjectorRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.lucentics.category.injector");
    }

    @Override
    @SuppressWarnings("removal")
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(InjectorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 36, 4);

        int daylight = recipe.getDayLightCondition();
        Component text = Component.translatable("jei.lucentics.info.injector.daylight_condition", daylight);
        guiGraphics.drawString(font, text, 2, 30, 0x404040, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InjectorRecipe recipe, IFocusGroup focuses) {
        recipe.getMainInput().ifPresent(sized ->
                builder.addSlot(RecipeIngredientRole.INPUT, 10, 5)
                        .setBackground(slot, -1, -1)
                        .addItemStacks(List.of(sized.getItems())));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 5)
                .setBackground(slot, -1, -1)
                .addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
    }
}