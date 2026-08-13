package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import io.github.rontyamc.lucentics.common.recipe.BaseRecipe;
import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public class InjectorRecipe<T extends RecipeInput> extends BaseRecipe<T, RecipeArguments> {

    public InjectorRecipe(IRecipeInfo recipeInfo, RecipeArguments args) {
        super(recipeInfo, args);
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return getIngredients().getFirst().test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return null;
    }
}
