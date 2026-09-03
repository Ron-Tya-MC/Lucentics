package io.github.rontyamc.lucentics.recipes.trail;

import io.github.rontyamc.lucentics.common.beam.Beam;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record TrailRecipeInput(ItemStack mainInput, List<Beam> beams) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return mainInput;
    }

    @Override
    public int size() {
        return 1;
    }
}
