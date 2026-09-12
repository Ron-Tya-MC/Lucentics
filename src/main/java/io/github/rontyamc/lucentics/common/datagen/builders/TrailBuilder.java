package io.github.rontyamc.lucentics.common.datagen.builders;

import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import io.github.rontyamc.lucentics.common.recipe.SizedThingIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Optional;

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

    public TrailBuilder input(RecipeArguments.OrderingInput input) {
        this.inputs.add(input);
        return this;
    }

    public TrailBuilder input(SizedThingIngredient ingredient, BehaviorType<?> requiredType, boolean notConsume) {
        this.inputs.add(new RecipeArguments.OrderingInput(ingredient, Optional.of(requiredType), notConsume));
        return this;
    }

    // アイテム
    public TrailBuilder input(SizedIngredient ingredient, BehaviorType<?> requiredType, boolean notConsume) {
        return input(SizedThingIngredient.of(ingredient), requiredType, notConsume);
    }

    public TrailBuilder input(SizedIngredient ingredient, BehaviorType<?> requiredType) {
        return input(SizedThingIngredient.of(ingredient), requiredType, false);
    }

    public TrailBuilder input(ItemLike item, int count, BehaviorType<?> requiredType, boolean notConsume) {
        return input(SizedIngredient.of(item, count), requiredType, notConsume);
    }

    public TrailBuilder input(ItemLike item, int count, BehaviorType<?> requiredType) {
        return input(SizedIngredient.of(item, count), requiredType, false);
    }

    public TrailBuilder input(ItemLike item, BehaviorType<?> requiredType, boolean notConsume) {
        return input(SizedIngredient.of(item, 1), requiredType, notConsume);
    }

    public TrailBuilder input(ItemLike item, BehaviorType<?> requiredType) {
        return input(SizedIngredient.of(item, 1), requiredType, false);
    }

    public TrailBuilder input(TagKey<Item> tag, int count, BehaviorType<?> requiredType, boolean notConsume) {
        return input(SizedIngredient.of(tag, count), requiredType, notConsume);
    }

    public TrailBuilder input(TagKey<Item> tag, int count, BehaviorType<?> requiredType) {
        return input(SizedIngredient.of(tag, count), requiredType, false);
    }

    public TrailBuilder input(TagKey<Item> tag, BehaviorType<?> requiredType, boolean notConsume) {
        return input(SizedIngredient.of(tag, 1), requiredType, notConsume);
    }

    public TrailBuilder input(TagKey<Item> tag, BehaviorType<?> requiredType) {
        return input(SizedIngredient.of(tag, 1), requiredType, false);
    }

    public TrailBuilder notConsumeInput(SizedIngredient ingredient, BehaviorType<?> requiredType) {
        return input(ingredient, requiredType, true);
    }

    public TrailBuilder notConsumeInput(ItemLike item, int count, BehaviorType<?> requiredType) {
        return input(SizedIngredient.of(item, count), requiredType, true);
    }

    public TrailBuilder notConsumeInput(ItemLike item, BehaviorType<?> requiredType) {
        return input(SizedIngredient.of(item, 1), requiredType, true);
    }

    // 流体
    public TrailBuilder input(SizedFluidIngredient ingredient, BehaviorType<?> requiredType, boolean notConsume) {
        return input(SizedThingIngredient.of(ingredient), requiredType, notConsume);
    }

    public TrailBuilder input(SizedFluidIngredient ingredient, BehaviorType<?> requiredType) {
        return input(SizedThingIngredient.of(ingredient), requiredType, false);
    }

    public TrailBuilder input(Fluid fluid, int amount, BehaviorType<?> requiredType, boolean notConsume) {
        return input(SizedFluidIngredient.of(fluid, amount), requiredType, notConsume);
    }

    public TrailBuilder input(Fluid fluid, int amount, BehaviorType<?> requiredType) {
        return input(fluid, amount, requiredType, false);
    }

    public TrailBuilder input(Fluid fluid, BehaviorType<?> requiredType, boolean notConsume) {
        return input(SizedFluidIngredient.of(fluid, FluidType.BUCKET_VOLUME), requiredType, notConsume);
    }

    public TrailBuilder input(Fluid fluid, BehaviorType<?> requiredType) {
        return input(fluid, FluidType.BUCKET_VOLUME, requiredType, false);
    }

    public TrailBuilder notConsumeInput(SizedFluidIngredient ingredient, BehaviorType<?> requiredType) {
        return input(ingredient, requiredType, true);
    }

    public TrailBuilder notConsumeInput(Fluid fluid, int amount, BehaviorType<?> requiredType) {
        return input(fluid, amount, requiredType, true);
    }

    public TrailBuilder notConsumeInput(Fluid fluid, BehaviorType<?> requiredType) {
        return input(fluid, FluidType.BUCKET_VOLUME, requiredType, true);
    }

    protected RecipeArguments.TrailInput build() {
        return new RecipeArguments.TrailInput(color.getSerializedName(), inputs);
    }
}
