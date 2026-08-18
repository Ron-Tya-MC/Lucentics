package io.github.rontyamc.lucentics.integration.jei.recipe_categories;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableRecipe;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class EngravingRecipeCategory implements IRecipeCategory<EngravingTableRecipe> {
    public static final RecipeType<EngravingTableRecipe> TYPE =
            RecipeType.create(Lucentics.MOD_ID, "engraving", EngravingTableRecipe.class);

    private static final int MAX_BEAMS = 6;
    private static final int MAX_DEVICES = 8;

    private static final int WIDTH = 160;
    private static final int HEADER_HEIGHT = 30;
    private static final int ROW_HEIGHT = 20;
    private static final int HEIGHT = HEADER_HEIGHT + MAX_BEAMS * ROW_HEIGHT;

    private static final int NODE_AREA_X = 20;
    private static final int NODE_AREA_WIDTH = 100;

    private final IDrawable background;
    private final IDrawable icon;

    public EngravingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(LucenticsBlockRegister.ENGRAVING_TABLE.asStack());
    }

    @Override
    public RecipeType<EngravingTableRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.lucentics.category.engraving");
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(EngravingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        List<RecipeArguments.TrailInput> trails = recipe.getTrailInputs();
        int rowCount = Math.min(trails.size(), MAX_BEAMS);

        for (int row = 0; row < rowCount; row++) {
            RecipeArguments.TrailInput trail = trails.get(row);
            int y = HEADER_HEIGHT + row * ROW_HEIGHT + 4;

            Colors color = Colors.byName(trail.color()).orElse(Colors.SUNLIGHT);
            int argb = 0xFF000000 | (color.getColorCode() & 0x00FFFFFF);
            guiGraphics.fill(4, y, 14, y + 10, argb);
        }

        if (trails.size() > MAX_BEAMS) {
            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.lucentics.info.engraving.too_much_beam"),
                    4, HEADER_HEIGHT + MAX_BEAMS * ROW_HEIGHT - 10, 0x404040, false);
        }
    }

    @SuppressWarnings("removal")
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EngravingTableRecipe recipe, IFocusGroup focuses) {
        recipe.getMainInput().ifPresent(sized ->
                builder.addSlot(RecipeIngredientRole.INPUT, 10, 5)
                        .addItemStacks(List.of(sized.getItems())));

        builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 28, 5)
                .addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));

        List<RecipeArguments.TrailInput> trails = recipe.getTrailInputs();
        int rowCount = Math.min(trails.size(), MAX_BEAMS);

        for (int row = 0; row < rowCount; row++) {
            RecipeArguments.TrailInput trail = trails.get(row);
            int y = HEADER_HEIGHT + row * ROW_HEIGHT + 2;

            List<RecipeArguments.OrderingInput> orderings = trail.inputs();
            int deviceCount = Math.min(orderings.size(), MAX_DEVICES);
            if (deviceCount == 0) continue;

            int cellWidth = NODE_AREA_WIDTH / deviceCount;

            for (int col = 0; col < deviceCount; col++) {
                RecipeArguments.OrderingInput ordering = orderings.get(col);
                int cellCenter = NODE_AREA_X + col * cellWidth + cellWidth / 2;
                int x = cellCenter - 8;

                ordering.ingredient().left().ifPresent(sized ->
                        builder.addSlot(RecipeIngredientRole.CATALYST, x, y)
                                .addItemStacks(List.of(sized.getItems()))
                                .addTooltipCallback((slotView, tooltip) -> {
                                    ordering.requiredType().ifPresent(type ->
                                            tooltip.add(Component.literal("Requires: " + type.getId())));
                                    if (ordering.notConsume()) {
                                        tooltip.add(Component.translatable("jei.lucentics.info.not_consume"));
                                    }
                                }));
            }
        }
    }
}
