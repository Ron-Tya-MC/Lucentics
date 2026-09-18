package io.github.rontyamc.lucentics.recipes.dyeing;

import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class DyeingRecipe implements Recipe<DyeingRecipeInput> {
    protected final IRecipeInfo recipeInfo;
    protected final DyeingRecipeArguments arguments;

    public DyeingRecipe(IRecipeInfo recipeInfo, DyeingRecipeArguments arguments) {
        this.recipeInfo = recipeInfo;
        this.arguments = arguments;
    }

    public DyeingRecipeArguments getArguments() {
        return arguments;
    }

    @Override
    public boolean matches(DyeingRecipeInput input, Level level) {
        return arguments.input().test(input.ingredient()) && arguments.color() == input.color();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(arguments.input());
        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return arguments.output();
    }

    @Override
    public ItemStack assemble(DyeingRecipeInput input, HolderLookup.Provider registries) {
        return arguments.output().copy();
    }

    @Override
    public RecipeType<?> getType() {
        return recipeInfo.getType();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return recipeInfo.getSerializer();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
