package io.github.rontyamc.lucentics.recipes.dyeing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Optional;

public record DyeingRecipeEntry(
        Ingredient ingredient, 
        SizedFluidIngredient fluid,
        ItemStack result,
        int processingDuration,
        Optional<ResourceLocation> recipeId
) {}
