package io.github.rontyamc.lucentics.recipes.trail;

import io.github.rontyamc.lucentics.common.ThingStack;
import io.github.rontyamc.lucentics.common.beam.Beam;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record TrailRecipeInput(ThingStack mainInput, List<Beam> beams) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return mainInput.asItemOrEmpty();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.mainInput.isEmpty() && this.beams.isEmpty();
    }

    public static TrailRecipeInput of(ThingStack ingredient, List<Beam> beams) {
        return new TrailRecipeInput(ingredient, beams);
    }

    public static TrailRecipeInput of(ItemStack stack, List<Beam> beams) {
        return new TrailRecipeInput(ThingStack.of(stack), beams);
    }

    public static TrailRecipeInput of(FluidStack stack, List<Beam> beams) {
        return new TrailRecipeInput(ThingStack.of(stack), beams);
    }
}
