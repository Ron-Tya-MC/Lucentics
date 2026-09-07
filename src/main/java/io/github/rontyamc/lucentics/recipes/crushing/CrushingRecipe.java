package io.github.rontyamc.lucentics.recipes.crushing;

import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class CrushingRecipe implements Recipe<CrushingRecipeInput> {

    protected final IRecipeInfo recipeInfo;
    protected final CrushingRecipeArguments arguments;
    protected NonNullList<List<RecipeArguments.WeightedOutput>> outputs;
    public CrushingRecipe(IRecipeInfo recipeInfo, CrushingRecipeArguments arguments) {
        this.recipeInfo = recipeInfo;
        this.arguments = arguments;
        this.outputs = arguments.outputs();
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

    public List<List<RecipeArguments.WeightedOutput>> getOutputs() {
        return arguments.outputs();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        for (var group : outputs) {
            for (var candidate : group) {
                Optional<ItemStack> item = candidate.content().left()
                        .map(RecipeArguments.WeightedOutput.ItemOutput::stack)
                        .filter(stack -> !stack.isEmpty());
                if (item.isPresent()) return item.get();
            }
        }
        return ItemStack.EMPTY;
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