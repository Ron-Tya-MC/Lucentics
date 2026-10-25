package io.github.rontyamc.lucentics.recipes.crushing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public record CrushingRecipeInput(BlockState blockState, ItemStack tool) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return tool;
    }

    @Override
    public int size() {
        return 1;
    }
}
