package io.github.rontyamc.lucentics.integration.jei.recipe_categories;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.behavior.BehaviorTypeBlockRegistry;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.integration.jei.CommonParts;
import io.github.rontyamc.lucentics.integration.jei.LucenticsJEIIntegration;
import io.github.rontyamc.lucentics.integration.jei.ProbabilisticOutputSlots;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class TrailRecipeCategory extends AbstractRecipeCategory<TrailRecipe> {
    // これ以上はGUIサイズによってはみ出す
    private static final int MAX_BEAMS = 4;
    private static final int MAX_DEVICES = 32;

    private static final int WIDTH = 160;
    private static final int HEADER_HEIGHT = 30;
    private static final int ROW_HEIGHT = 30;
    private static final int FOOTER_HEIGHT = 20;
    private static final int HEIGHT = HEADER_HEIGHT + MAX_BEAMS * ROW_HEIGHT + FOOTER_HEIGHT;

    private static final int NODE_AREA_LEFT_SIDE = 25;
    private static final int NODE_AREA_WIDTH = 110;
    private static final int DEVICE_SLOT_OFFSET = 10;
    private static final int ITEM_SLOT_OFFSET = 0;

    private static final ResourceLocation ARROW_ALPHA_TEXTURE =
            Lucentics.defaultLocation("textures/gui/jei/engraving_arrow_alpha.png");
    private static final ResourceLocation ARROW_OVERLAY_TEXTURE =
            Lucentics.defaultLocation("textures/gui/jei/engraving_arrow_overlay.png");


    private final IDrawableStatic arrowAlpha;
    private final IDrawableStatic arrowOverlay;

    public TrailRecipeCategory(RecipeType<TrailRecipe> recipeType, Component title, IDrawable icon, IGuiHelper guiHelper) {
        super(recipeType, title, icon, WIDTH, HEIGHT);

        this.arrowAlpha = guiHelper.drawableBuilder(ARROW_ALPHA_TEXTURE, 0, 0, 120, 14)
                .setTextureSize(120, 14)
                .build();
        this.arrowOverlay = guiHelper.drawableBuilder(ARROW_OVERLAY_TEXTURE, 0, 0, 120, 14)
                .setTextureSize(120, 14)
                .build();
    }

    @Override
    public void draw(TrailRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        List<RecipeArguments.TrailInput> trails = recipe.getTrailInputs();
        int rowCount = Math.min(trails.size(), MAX_BEAMS);

        for (int row = 0; row < rowCount; row++) {
            RecipeArguments.TrailInput trail = trails.get(row);
            int y = HEADER_HEIGHT + row * ROW_HEIGHT + 19;

            Colors color = Colors.byName(trail.color()).orElse(Colors.SUNLIGHT);
            drawColored(guiGraphics, arrowAlpha, 20, y, color);
            arrowOverlay.draw(guiGraphics, 20, y);

            guiGraphics.drawString(Minecraft.getInstance().font, row + 1 + ".",
                    8, y+2, 0xFF808080, false);
        }

        if (trails.size() > MAX_BEAMS) {
            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.lucentics.info.engraving.too_much_beam"),
                    4, HEADER_HEIGHT + MAX_BEAMS * ROW_HEIGHT - 10, 0x404040, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.lucentics.info.engraving.too_much_beam2"),
                    4, HEADER_HEIGHT + MAX_BEAMS * ROW_HEIGHT, 0x404040, false);
        }
    }

    public void drawArrowLight(GuiGraphics guiGraphics) {
        CommonParts.arrowLight.draw(guiGraphics, WIDTH / 2 - 24 , 4);
    }

    public void drawArrowNormal(GuiGraphics guiGraphics) {
        CommonParts.arrowNormal48.draw(guiGraphics, WIDTH / 2 - 24 , 4);
    }

    private static void drawColored(GuiGraphics guiGraphics, IDrawableStatic drawable, int x, int y, Colors color) {
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

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TrailRecipe recipe, IFocusGroup focuses) {
        recipe.getMainInput().asItem().ifPresent(sized ->
                builder.addInputSlot(WIDTH / 2 - 50, 5)
                        .setBackground(CommonParts.slot_normal, -1, -1)
                        .addIngredients(sized.ingredient()));

        ProbabilisticOutputSlots.addSlots(builder, recipe.getOutputs(), WIDTH / 2 + 33, 5, 20, Integer.MAX_VALUE);

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
                int cellCenter = NODE_AREA_LEFT_SIDE + col * cellWidth + cellWidth / 2;
                int x = cellCenter - 8;

                ordering.requiredType().ifPresent(type -> {
                    List<ItemStack> deviceBlocks = BehaviorTypeBlockRegistry.getProviders(type);
                    if (!deviceBlocks.isEmpty()) {
                        builder.addSlot(RecipeIngredientRole.CATALYST, x, y + DEVICE_SLOT_OFFSET)
                                .addItemStacks(deviceBlocks);
                    }
                });

                ordering.ingredient().asItem().ifPresent(sized ->
                        builder.addSlot(RecipeIngredientRole.CATALYST, x, y + ITEM_SLOT_OFFSET)
                                .addItemStacks(List.of(sized.getItems()))
                                .addRichTooltipCallback((slotView, tooltip) -> {
                                    if (ordering.notConsume()) {
                                        tooltip.add(Component.translatable("jei.lucentics.info.not_consume"));
                                    }
                                })
                );
            }
        }
    }

    protected void addDaylightCondition(IRecipeExtrasBuilder builder, TrailRecipe recipe) {
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

    protected void addProcessingDuration(IRecipeExtrasBuilder builder, TrailRecipe recipe) {
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
