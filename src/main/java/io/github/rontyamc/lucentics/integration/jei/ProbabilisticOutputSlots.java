package io.github.rontyamc.lucentics.integration.jei;

import io.github.rontyamc.lucentics.common.recipe.RecipeArguments.WeightedOutput;
import io.github.rontyamc.lucentics.common.util.MiscUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

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
            List<ItemStack> displayStacks = collectDisplayStacks(group);
            if (displayStacks.isEmpty()) continue;

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
                    .addItemStacks(displayStacks)
                    .addRichTooltipCallback((view, tooltip) -> appendTooltip(view, tooltip, group, totalWeight));
        }
    }

    private static List<ItemStack> collectDisplayStacks(List<WeightedOutput> group) {
        List<ItemStack> stacks = new ArrayList<>();
        for (WeightedOutput w : group) {
            w.content().left().ifPresent(item -> {
                int min = item.count().getMinValue();
                int max = item.count().getMaxValue();
                stacks.add(item.stack().copyWithCount(1));
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

            Optional<ItemStack> displayed = slotView.get().getDisplayedItemStack();
            if (displayed.isEmpty()) continue;

            String label = findMatchingRangeLabel(group, displayed.get());
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

    private static String findMatchingRangeLabel(List<WeightedOutput> group, ItemStack displayed) {
        WeightedOutput matchedOutput = findMatchingOutput(group, displayed);
        if (matchedOutput == null) return null;

        Optional<WeightedOutput.ItemOutput> item = matchedOutput.content().left();

        int min = item.get().count().getMinValue();
        int max = item.get().count().getMaxValue();
        if (min == max && min == 1) return "";
        return min == max ? String.valueOf(min) : min + "-" + max;
    }

    private static WeightedOutput findMatchingOutput(List<WeightedOutput> group, ItemStack displayed) {
        for (WeightedOutput w : group) {
            Optional<WeightedOutput.ItemOutput> item = w.content().left();
            if (item.isPresent() && ItemStack.isSameItem(item.get().stack(), displayed)) {
                return w;
            }
        }
        return null;
    }

    private static String slotName(int index) {
        return "lucentics:prob_output_slot_" + index;
    }

    private static void appendTooltip(IRecipeSlotView view, ITooltipBuilder tooltip, List<WeightedOutput> group, int totalWeight) {
        Optional<ItemStack> displayed = view.getDisplayedItemStack();
        if (displayed.isEmpty()) return;

        WeightedOutput matchedOutput = findMatchingOutput(group, displayed.get());
        if (matchedOutput == null) return;

        matchedOutput.content().left().ifPresent(item -> {
            Component countLabel = item.count().getMinValue() != item.count().getMaxValue()
                    ? Component.translatable("jei.lucentics.info.count", item.count().getMinValue() + "-" + item.count().getMaxValue())
                    : Component.empty();
            Component weightLabel = group.size() > 1
                    ? Component.translatable("jei.lucentics.info.weight", matchedOutput.weight(), MiscUtil.shapePercentage(100.0 * matchedOutput.weight() / totalWeight))
                    : Component.empty();
            Component probabilityLabel = matchedOutput.probability() < 1.0f
                    ? Component.translatable("jei.lucentics.info.probability", MiscUtil.shapePercentage(100.0 * matchedOutput.probability()))
                    : Component.empty();
            tooltip.add(countLabel);
            tooltip.add(weightLabel);
            tooltip.add(probabilityLabel);
        });
    }
}
