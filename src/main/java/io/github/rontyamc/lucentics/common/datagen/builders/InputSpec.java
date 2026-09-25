package io.github.rontyamc.lucentics.common.datagen.builders;

import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments.OrderingInput;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import io.github.rontyamc.lucentics.common.recipe.SizedThingIngredient;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Optional;

public class InputSpec {
    private final SizedThingIngredient ingredient;
    Optional<BehaviorType<?>> requiredType = Optional.empty();
    int damageItem = 0;
    boolean notConsume = false;

    private InputSpec(SizedThingIngredient ingredient) {
        this.ingredient = ingredient;
    }

    public static InputSpec item(SizedThingIngredient ingredient) {
        return new InputSpec(ingredient);
    }

    public static InputSpec item(ItemStack stack) {
        return item(SizedThingIngredient.of(stack));
    }


    public static InputSpec item(ItemLike item, int count) {
        return item(new ItemStack(item, count));
    }

    public static InputSpec item(ItemLike item) {
        return item(new ItemStack(item, 1));
    }

    public static InputSpec fluid(FluidStack stack) {
        return item(SizedThingIngredient.of(stack));
    }

    public static InputSpec fluid(Fluid fluid, int amount) {
        return fluid(new FluidStack(fluid, amount));
    }

    public static InputSpec fluid(Fluid fluid) {
        return fluid(new FluidStack(fluid, FluidType.BUCKET_VOLUME));
    }

    public static InputSpec item(TagKey<Item> tag, int count) {
        return item(SizedThingIngredient.of(SizedIngredient.of(tag, count)));
    }

    public static InputSpec item(TagKey<Item> tag) {
        return item(SizedThingIngredient.of(SizedIngredient.of(tag, 1)));
    }

    public static InputSpec fluid(TagKey<Fluid> tag, int amount) {
        return item(SizedThingIngredient.of(SizedFluidIngredient.of(tag, amount)));
    }

    public static InputSpec fluid(TagKey<Fluid> tag) {
        return item(SizedThingIngredient.of(SizedFluidIngredient.of(tag, FluidType.BUCKET_VOLUME)));
    }

    public InputSpec requiredType(BehaviorType type) {
        this.requiredType = Optional.of(type);
        return this;
    }

    public InputSpec damageItem(int damage) {
        this.damageItem = damage;
        return this;
    }

    public InputSpec notConsume(boolean notConsume) {
        this.notConsume = notConsume;
        return this;
    }

    OrderingInput build() {
        return new OrderingInput(ingredient, requiredType, damageItem, notConsume);
    }
}
