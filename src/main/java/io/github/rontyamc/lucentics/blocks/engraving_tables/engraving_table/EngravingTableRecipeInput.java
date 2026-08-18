package io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table;

import io.github.rontyamc.lucentics.common.beam.Beam;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record EngravingTableRecipeInput(ItemStack mainInput, List<Beam> beams) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return mainInput;
    }

    @Override
    public int size() {
        return 1;
    }
}
