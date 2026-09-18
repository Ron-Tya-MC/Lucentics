package io.github.rontyamc.lucentics.recipes.dyeing;

import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record DyeingRecipeEntry(Ingredient ingredient, Colors color, int liquidAmount, ItemStack result) {}
