package io.github.rontyamc.lucentics.common.datagen.builders;

import com.mojang.datafixers.util.Either;
import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBehavior;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

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

    public TrailBuilder input(SizedIngredient ingredient, BehaviorType<?> requiredType, boolean notConsume) {
        this.inputs.add(new RecipeArguments.OrderingInput(Either.left(ingredient), Optional.of(requiredType), notConsume));
        return this;
    }

    public TrailBuilder input(SizedIngredient ingredient, BehaviorType<?> requiredType) {
        return input(ingredient, requiredType, false);
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

    protected RecipeArguments.TrailInput build() {
        return new RecipeArguments.TrailInput(color.getSerializedName(), inputs);
    }
}
