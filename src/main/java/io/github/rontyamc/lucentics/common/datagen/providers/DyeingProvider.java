package io.github.rontyamc.lucentics.common.datagen.providers;

import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import net.minecraft.data.recipes.RecipeOutput;
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
                            .liquidAmount(2000));
        }
    }
}
