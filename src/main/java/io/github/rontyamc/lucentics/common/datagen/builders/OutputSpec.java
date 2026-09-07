package io.github.rontyamc.lucentics.common.datagen.builders;

import com.mojang.datafixers.util.Either;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments.WeightedOutput;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public final class OutputSpec {
    private final Either<ItemStack, FluidStack> stack;
    private IntProvider amount;
    private float probability = 1.0f;
    private int weight = 1;

    private OutputSpec(Either<ItemStack, FluidStack> stack, IntProvider amount) {
        this.stack = stack;
        this.amount = amount;
    }

    public static OutputSpec of(ItemLike item) {
        return of(new ItemStack(item, 1));
    }

    public static OutputSpec of(ItemStack stack) {
        return new OutputSpec(Either.left(stack.copyWithCount(1)), ConstantInt.of(stack.getCount()));
    }

    public static OutputSpec of(Fluid fluid) {
        return of(new FluidStack(fluid, 1));
    }

    public static OutputSpec of(FluidStack stack) {
        return new OutputSpec(Either.right(stack), ConstantInt.of(stack.getAmount()));
    }

    // count()とamount()は実質同じ 気に入った方を使ってください
    public OutputSpec count(int count) {
        this.amount = ConstantInt.of(count);
        return this;
    }

    public OutputSpec count(IntProvider count) {
        this.amount = count;
        return this;
    }

    public OutputSpec amount(int amount) {
        this.amount = ConstantInt.of(amount);
        return this;
    }

    public OutputSpec amount(IntProvider amount) {
        this.amount = amount;
        return this;
    }

    public OutputSpec probability(float probability) {
        this.probability = probability;
        return this;
    }

    public OutputSpec weight(int weight) {
        this.weight = weight;
        return this;
    }

    WeightedOutput build() {
        Either<WeightedOutput.ItemOutput, WeightedOutput.FluidOutput> content = stack.mapBoth(
                item -> new WeightedOutput.ItemOutput(item, amount),
                fluid -> new WeightedOutput.FluidOutput(fluid, amount)
        );
        return new WeightedOutput(content, probability, weight);
    }
}
