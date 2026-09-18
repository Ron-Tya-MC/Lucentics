package io.github.rontyamc.lucentics.integration.jei;

import io.github.rontyamc.lucentics.common.recipe.RecipeArguments.WeightedOutput;
import io.github.rontyamc.lucentics.common.util.MiscUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProbabilisticOutputSlots {
    private ProbabilisticOutputSlots(IGuiHelper guiHelper) {}

    public static void addSlots(IRecipeLayoutBuilder builder, List<List<WeightedOutput>> groups,
                                int startX, int startY, int spacingX, int maxSlots) {
        int size = Math.min(groups.size(), maxSlots);
        for (int i = 0; i < size; i++) {
            List<WeightedOutput> group = groups.get(i);

            List<ItemStack> displayItems = collectDisplayItems(group);
            List<FluidStack> displayFluids = collectDisplayFluids(group);

            if (displayItems.isEmpty() && displayFluids.isEmpty()) continue;

            int slotX = startX + (i % 2) * spacingX;
            int slotY = size <= 2 ? startY : startY - (i / 2) * spacingX;
            int totalWeight = group.stream().mapToInt(WeightedOutput::weight).sum();

            IDrawableStatic background;
            if (group.size() > 1) background = CommonParts.slot_blue_blur;
            else if (group.getFirst().probability() < 1.0f) background = CommonParts.slot_yellow_blur;
            else background = CommonParts.slot_normal;

            builder.addOutputSlot(slotX, slotY)
                    .setSlotName(slotName(i))
                    .setBackground(background, -1, -1)
                    .addItemStacks(displayItems)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, displayFluids)
                    .addRichTooltipCallback((view, tooltip) -> appendTooltip(view, tooltip, group, totalWeight));
        }
    }

    private static List<ItemStack> collectDisplayItems(List<WeightedOutput> group) {
        List<ItemStack> stacks = new ArrayList<>();
        for (WeightedOutput w : group) {
            w.content().left().ifPresent(item -> {
                stacks.add(item.stack().copyWithCount(1));
            });
        }
        return stacks;
    }
    private static List<FluidStack> collectDisplayFluids(List<WeightedOutput> group) {
        List<FluidStack> stacks = new ArrayList<>();
        for (WeightedOutput w : group) {
            w.content().right().ifPresent(fluid -> {
                stacks.add(fluid.stack().copyWithAmount(FluidType.BUCKET_VOLUME));
            });
        }
        return stacks;
    }

    public static void drawRangeBadges(GuiGraphics guiGraphics, IRecipeSlotsView recipeSlotsView,
                                       List<List<WeightedOutput>> groups, int startX, int startY, int spacingX, int maxSlots) {
        Font font = Minecraft.getInstance().font;
        int size = Math.min(groups.size(), maxSlots);

        for (int i = 0; i < size; i++) {
            List<WeightedOutput> group = groups.get(i);

            Optional<IRecipeSlotView> slotView = recipeSlotsView.findSlotByName(slotName(i));
            if (slotView.isEmpty()) continue;

            String label = findMatchingRangeLabel(group, slotView.get());
            if (label == null) continue;

            int slotX = startX + (i % 2) * spacingX;
            int slotY = size <= 2 ? startY : startY - (i / 2) * spacingX;
            int textWidth = font.width(label);
            float scale = Math.clamp(15.0f / textWidth, 0.0f, 1.0f);
            float textX = (slotX + 0.75f) / scale;
            if (scale == 1.0f) textX = slotX + 16 - textWidth;
            float textY = (slotY + 15 - 7 * scale) / scale;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(scale, scale, 1.0f);
            guiGraphics.pose().translate(0, 0, 300);
            guiGraphics.drawString(font, label, textX, textY, 0xFFFFFF, true);
            guiGraphics.pose().popPose();
        }
    }

    private static String findMatchingRangeLabel(List<WeightedOutput> group, IRecipeSlotView view) {
        WeightedOutput matchedOutput = findMatchingOutput(group, view);
        if (matchedOutput == null) return null;

        Optional<WeightedOutput.ItemOutput> item = matchedOutput.content().left();
        if (item.isEmpty()) return null;

        int min = item.get().count().getMinValue();
        int max = item.get().count().getMaxValue();
        if (min == max && min == 1) return "";
        return min == max ? String.valueOf(min) : min + "-" + max;
    }

    private static WeightedOutput findMatchingOutput(List<WeightedOutput> group, IRecipeSlotView view) {
        Optional<ItemStack> item = view.getDisplayedItemStack();
        if (item.isPresent()) {
            for (WeightedOutput w : group) {
                var output = w.content().left();
                if (output.isPresent() && ItemStack.isSameItem(output.get().stack(), item.get())) return w;
            }
        }

        Optional<FluidStack> fluid = view.getDisplayedIngredient(NeoForgeTypes.FLUID_STACK);
        if (fluid.isPresent()) {
            for (WeightedOutput w : group) {
                var output = w.content().right();
                if (output.isPresent() && FluidStack.isSameFluid(output.get().stack(), fluid.get())) return w;
            }
        }

        return null;
    }

    private static String slotName(int index) {
        return "lucentics:prob_output_slot_" + index;
    }

    private static void appendTooltip(IRecipeSlotView view, ITooltipBuilder tooltip, List<WeightedOutput> group, int totalWeight) {
        WeightedOutput matchedOutput = findMatchingOutput(group, view);
        if (matchedOutput == null) return;

        matchedOutput.content().map(item -> {
            Component countLabel = item.count().getMinValue() != item.count().getMaxValue()
                    ? Component.translatable("jei.lucentics.info.count", item.count().getMinValue() + "-" + item.count().getMaxValue()).withColor(0xCCFFCC)
                    : Component.empty();
            Component weightLabel = group.size() > 1
                    ? Component.translatable("jei.lucentics.info.weight", matchedOutput.weight(), MiscUtil.shapePercentage(100.0 * matchedOutput.weight() / totalWeight)).withColor(0xCCCCFF)
                    : Component.empty();
            Component probabilityLabel = matchedOutput.probability() < 1.0f
                    ? Component.translatable("jei.lucentics.info.probability", MiscUtil.shapePercentage(100.0 * matchedOutput.probability())).withColor(0xFFFFCC)
                    : Component.empty();
            tooltip.add(countLabel);
            tooltip.add(weightLabel);
            tooltip.add(probabilityLabel);
            return null;
        },
        fluid -> {
            Component countLabel = fluid.amount().getMinValue() != fluid.amount().getMaxValue()
                    ? Component.translatable("jei.lucentics.info.count", fluid.amount().getMinValue() + "-" + fluid.amount().getMaxValue()).withColor(0xCCFFCC)
                    : Component.empty();
            Component weightLabel = group.size() > 1
                    ? Component.translatable("jei.lucentics.info.weight", matchedOutput.weight(), MiscUtil.shapePercentage(100.0 * matchedOutput.weight() / totalWeight)).withColor(0xCCCCFF)
                    : Component.empty();
            Component probabilityLabel = matchedOutput.probability() < 1.0f
                    ? Component.translatable("jei.lucentics.info.probability", MiscUtil.shapePercentage(100.0 * matchedOutput.probability())).withColor(0xFFFFCC)
                    : Component.empty();
            tooltip.add(countLabel);
            tooltip.add(weightLabel);
            tooltip.add(probabilityLabel);
            return null;
        });
    }
}
