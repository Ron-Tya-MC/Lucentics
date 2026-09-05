package io.github.rontyamc.lucentics.recipes.crushing;

import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CrushingRecipe implements Recipe<CrushingRecipeInput> {

    protected final IRecipeInfo recipeInfo;
    protected final CrushingRecipeArguments arguments;
    public CrushingRecipe(IRecipeInfo recipeInfo, CrushingRecipeArguments arguments) {
        this.recipeInfo = recipeInfo;
        this.arguments = arguments;
    }

    @Override
    public boolean matches(CrushingRecipeInput input, Level level) {
        return input.blockState().is(arguments.block()) &&
                arguments.tool().test(input.tool());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return arguments.outputs().stream()
                .flatMap(o -> o.item().stream())
                .findFirst()
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack assemble(CrushingRecipeInput input, HolderLookup.Provider registries) {
        return getResultItem(registries).copy();
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

    public CrushingRecipeArguments getArguments() {
        return arguments;
    }
}