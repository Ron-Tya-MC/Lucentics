package io.github.rontyamc.lucentics.datagen.providers.recipe;

import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

public class InjectingProvider {

    public InjectingProvider() {}

    public static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.leaveFolder();

        provider.generic(LucenticsItemRegister.DUSK_BRICK)
                .injecting(b -> b.input(Items.BRICK)
                        .duration(100)
                        .daylight(7));

        provider.generic(LucenticsBlockRegister.DAWNSTONE)
                .injecting(b -> b.input(Items.DIORITE)
                        .duration(100)
                        .daylight(7));
    }
}
