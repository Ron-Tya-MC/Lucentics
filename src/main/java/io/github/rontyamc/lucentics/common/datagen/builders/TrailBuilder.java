package io.github.rontyamc.lucentics.common.datagen.builders;

import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

public class TrailBuilder {
    private final Colors color;
    private final NonNullList<RecipeArguments.OrderingInput> inputs;

    public TrailBuilder(Colors color, NonNullList<RecipeArguments.OrderingInput> inputs) {
        this.color = color;
        this.inputs = inputs;
    }

    public static TrailBuilder create(Colors color, NonNullList<RecipeArguments.OrderingInput> inputs) {
        return new TrailBuilder(color, inputs);
    }

    public static TrailBuilder create(Colors color) {
        return create(color, NonNullList.create());
    }

    public TrailBuilder input(InputSpec spec) {
        this.inputs.add(spec.build());
        return this;
    }

    public TrailBuilder input(ItemLike item, int count) {
        return input(InputSpec.item(item, count));
    }

    public TrailBuilder input(ItemLike item) {
        return input(InputSpec.item(item));
    }

    public TrailBuilder input(Fluid fluid, int amount) {
        return input(InputSpec.fluid(fluid, amount));
    }

    public TrailBuilder input(Fluid fluid) {
        return input(InputSpec.fluid(fluid));
    }

    protected RecipeArguments.TrailInput build() {
        return new RecipeArguments.TrailInput(color.getSerializedName(), inputs);
    }
}
