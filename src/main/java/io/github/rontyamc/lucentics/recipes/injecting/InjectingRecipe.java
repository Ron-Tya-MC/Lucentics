package io.github.rontyamc.lucentics.recipes.injecting;

import io.github.rontyamc.lucentics.common.recipe.BaseRecipe;
import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import net.minecraft.world.level.Level;

public class InjectingRecipe extends BaseRecipe<InjectingRecipeInput, RecipeArguments> {

    public InjectingRecipe(IRecipeInfo recipeInfo, RecipeArguments args) {
        super(recipeInfo, args);
    }

    @Override
    public boolean matches(InjectingRecipeInput input, Level level) {
        return mainInput.test(input.input());
    }
}