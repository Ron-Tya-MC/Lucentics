package io.github.rontyamc.lucentics.recipes.dyeing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public record DyeingRecipeInput(ItemStack ingredient, FluidStack fluid) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return ingredient;
    }

    @Override
    public int size() {
        return 1;
    }
}