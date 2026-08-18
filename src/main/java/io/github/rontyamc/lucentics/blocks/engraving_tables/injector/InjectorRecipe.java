package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import io.github.rontyamc.lucentics.common.recipe.BaseRecipe;
import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InjectorRecipe extends BaseRecipe<InjectorRecipeInput, RecipeArguments> {

    public InjectorRecipe(IRecipeInfo recipeInfo, RecipeArguments args) {
        super(recipeInfo, args);

        if (outputs.size() != 1 || outputs.getFirst().item().isEmpty()) {
            throw new IllegalStateException(
                    "lucentics:injector recipe '" + recipeInfo.getId() +
                            "' must have exactly one item output (got " + outputs.size() + " outputs)");
        }
        if (outputs.getFirst().fluid().isPresent()) {
            throw new IllegalStateException(
                    "lucentics:injector recipe '" + recipeInfo.getId() +
                            "' does not support fluid outputs");
        }
    }

    @Override
    public boolean matches(InjectorRecipeInput input, Level level) {
        return mainInput.map(sized -> sized.test(input.input())).orElse(false);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return outputs.getFirst().item().orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack assemble(InjectorRecipeInput input, HolderLookup.Provider registries) {
        return getResultItem(registries).copy();
    }
}