package io.github.rontyamc.lucentics.recipes.dyeing;

import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record DyeingRecipeInput(ItemStack ingredient, Colors color) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return ingredient;
    }

    @Override
    public int size() {
        return 1;
    }
}