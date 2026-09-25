package io.github.rontyamc.lucentics.common.datagen.providers.recipe;

import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;

public class DyeingProvider {
    public DyeingProvider() {}

    protected static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.leaveFolder();

        for (Colors color : Colors.values()) {
            if (color.equals(Colors.SUNLIGHT)) continue;
            provider.generic(LucenticsBlockRegister.COLORED_PRISM_BLANK.get(color))
                    .dyeing(b -> b.input(Ingredient.of(LucenticsBlockRegister.PRISM_BLANK))
                            .color(color)
                            .liquidAmount(500)
                            .duration(30));
        }

        for (Colors color : Colors.values()) {
            if (color.equals(Colors.SUNLIGHT)) continue;
            provider.generic(() -> ItemUtil.byId(ResourceLocation.withDefaultNamespace(color.getName() + "_banner")))
                    .dyeing(b -> b.input(Ingredient.of(ItemTags.BANNERS))
                            .color(color)
                            .liquidAmount(400)
                            .duration(15));
        }

        for (Colors color : Colors.values()) {
            if (color.equals(Colors.SUNLIGHT)) continue;
            provider.generic(() -> ItemUtil.byId(ResourceLocation.withDefaultNamespace(color.getName() + "_candle")))
                    .dyeing(b -> b.input(Ingredient.of(ItemTags.CANDLES))
                            .color(color)
                            .liquidAmount(400)
                            .duration(15));
        }
    }
}
