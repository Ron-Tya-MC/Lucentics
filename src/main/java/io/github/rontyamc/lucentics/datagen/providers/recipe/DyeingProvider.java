package io.github.rontyamc.lucentics.datagen.providers.recipe;

import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsFluidRegister;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

public class DyeingProvider {
    public DyeingProvider() {}

    public static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.leaveFolder();

        for (Colors color : Colors.values()) {
            if (color.equals(Colors.SUNLIGHT)) continue;
            provider.generic(LucenticsBlockRegister.COLORED_PRISM_BLANK.get(color))
                    .dyeing(b -> b.item(LucenticsBlockRegister.PRISM_BLANK)
                            .fluid(LucenticsFluidRegister.DYE_LIQUIDS.get(color).get().getSource())
                            .fluidAmount(500)
                            .duration(30));
        }

        for (Colors color : Colors.values()) {
            if (color.equals(Colors.SUNLIGHT)) continue;
            provider.generic(() -> ItemUtil.byId(ResourceLocation.withDefaultNamespace(color.getName() + "_banner")))
                    .dyeing(b -> b.item(Ingredient.of(ItemTags.BANNERS))
                            .fluid(LucenticsFluidRegister.DYE_LIQUIDS.get(color).get().getSource())
                            .fluidAmount(400)
                            .duration(15));
        }

        for (Colors color : Colors.values()) {
            if (color.equals(Colors.SUNLIGHT)) continue;
            provider.generic(() -> ItemUtil.byId(ResourceLocation.withDefaultNamespace(color.getName() + "_candle")))
                    .dyeing(b -> b.item(Ingredient.of(ItemTags.CANDLES))
                            .fluid(LucenticsFluidRegister.DYE_LIQUIDS.get(color).get().getSource())
                            .fluidAmount(400)
                            .duration(15));
        }

        provider.enterFolder("oxidizing");

        provider.generic(() -> Blocks.EXPOSED_COPPER)
                .dyeing(b -> b.item(() -> Items.COPPER_BLOCK)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.WEATHERED_COPPER)
                .dyeing(b -> b.item(() -> Items.EXPOSED_COPPER)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.OXIDIZED_COPPER)
                .dyeing(b -> b.item(() -> Items.WEATHERED_COPPER)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));

        provider.generic(() -> Blocks.EXPOSED_CUT_COPPER)
                .dyeing(b -> b.item(() -> Items.CUT_COPPER)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.WEATHERED_CUT_COPPER)
                .dyeing(b -> b.item(() -> Items.EXPOSED_CUT_COPPER)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.OXIDIZED_CUT_COPPER)
                .dyeing(b -> b.item(() -> Items.WEATHERED_CUT_COPPER)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));

        provider.generic(() -> Blocks.EXPOSED_CUT_COPPER_SLAB)
                .dyeing(b -> b.item(() -> Items.CUT_COPPER_SLAB)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.WEATHERED_CUT_COPPER_SLAB)
                .dyeing(b -> b.item(() -> Items.EXPOSED_CUT_COPPER_SLAB)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.OXIDIZED_CUT_COPPER_SLAB)
                .dyeing(b -> b.item(() -> Items.WEATHERED_CUT_COPPER_SLAB)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));

        provider.generic(() -> Blocks.EXPOSED_CUT_COPPER_STAIRS)
                .dyeing(b -> b.item(() -> Items.CUT_COPPER_STAIRS)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.WEATHERED_CUT_COPPER_STAIRS)
                .dyeing(b -> b.item(() -> Items.EXPOSED_CUT_COPPER_STAIRS)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.OXIDIZED_CUT_COPPER_STAIRS)
                .dyeing(b -> b.item(() -> Items.WEATHERED_CUT_COPPER_STAIRS)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));

        provider.generic(() -> Blocks.EXPOSED_CHISELED_COPPER)
                .dyeing(b -> b.item(() -> Items.CHISELED_COPPER)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.WEATHERED_CHISELED_COPPER)
                .dyeing(b -> b.item(() -> Items.EXPOSED_CHISELED_COPPER)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.OXIDIZED_CHISELED_COPPER)
                .dyeing(b -> b.item(() -> Items.WEATHERED_CHISELED_COPPER)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));

        provider.generic(() -> Blocks.EXPOSED_COPPER_BULB)
                .dyeing(b -> b.item(() -> Items.COPPER_BULB)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.WEATHERED_COPPER_BULB)
                .dyeing(b -> b.item(() -> Items.EXPOSED_COPPER_BULB)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.OXIDIZED_COPPER_BULB)
                .dyeing(b -> b.item(() -> Items.WEATHERED_COPPER_BULB)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));

        provider.generic(() -> Blocks.EXPOSED_COPPER_DOOR)
                .dyeing(b -> b.item(() -> Items.COPPER_DOOR)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.WEATHERED_COPPER_DOOR)
                .dyeing(b -> b.item(() -> Items.EXPOSED_COPPER_DOOR)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.OXIDIZED_COPPER_DOOR)
                .dyeing(b -> b.item(() -> Items.WEATHERED_COPPER_DOOR)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));

        provider.generic(() -> Blocks.EXPOSED_COPPER_GRATE)
                .dyeing(b -> b.item(() -> Items.COPPER_GRATE)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.WEATHERED_COPPER_GRATE)
                .dyeing(b -> b.item(() -> Items.EXPOSED_COPPER_GRATE)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.OXIDIZED_COPPER_GRATE)
                .dyeing(b -> b.item(() -> Items.WEATHERED_COPPER_GRATE)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));

        provider.generic(() -> Blocks.EXPOSED_COPPER_TRAPDOOR)
                .dyeing(b -> b.item(() -> Items.COPPER_TRAPDOOR)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.WEATHERED_COPPER_TRAPDOOR)
                .dyeing(b -> b.item(() -> Items.EXPOSED_COPPER_TRAPDOOR)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));
        provider.generic(() -> Blocks.OXIDIZED_COPPER_TRAPDOOR)
                .dyeing(b -> b.item(() -> Items.WEATHERED_COPPER_TRAPDOOR)
                        .fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource())
                        .fluidAmount(20)
                        .duration(10));

        provider.leaveFolder();
    }
}
