package io.github.rontyamc.lucentics.common.datagen.builders;

import com.mojang.datafixers.util.Either;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.recipes.injecting.InjectingRecipe;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

public class InjectingBuilder implements RecipeBuilder {
    private SizedIngredient input;
    private final ItemStack result;
    protected String suffix;
    private int processingDuration = 100;
    private int daylightCondition = 0;

    public InjectingBuilder(SizedIngredient input, ItemLike result, int count) {
        this.input = input;
        this.result = new ItemStack(result, count);
        this.suffix = "";
    }

    public static InjectingBuilder create(SizedIngredient input, ItemLike result, int count) {
        return new InjectingBuilder(input, result, count);
    }
    public static InjectingBuilder create(SizedIngredient input, ItemLike result) {
        return create(input, result, 1);
    }

    public InjectingBuilder input(SizedIngredient input) {
        this.input = input;
        return this;
    }

    public InjectingBuilder input(ItemLike input, int count) {
        return input(SizedIngredient.of(input, count));
    }

    public InjectingBuilder input(ItemLike input) {
        return input(input, 1);
    }

    public InjectingBuilder duration(int ticks) {
        this.processingDuration = ticks;
        return this;
    }

    public InjectingBuilder daylight(int level) {
        this.daylightCondition = level;
        return this;
    }

    @Override
    public InjectingBuilder unlockedBy(String criterionName, Criterion<?> criterion) {
        return this;
    }

    @Override
    public InjectingBuilder group(String groupName) {
        return this;
    }

    public InjectingBuilder suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(RecipeOutput output) {
        ResourceLocation defaultId = RecipeBuilder.getDefaultRecipeId(getResult());
        ResourceLocation id = Lucentics.defaultLocation("injecting/" + defaultId.getPath() + suffix);
        save(output, id);
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        NonNullList<RecipeArguments.Output> outputs = NonNullList.create();
        outputs.add(new RecipeArguments.Output(Optional.of(result), Optional.empty()));

        RecipeArguments args = new RecipeArguments(
                Either.left(input),
                NonNullList.create(),
                outputs,
                processingDuration,
                daylightCondition
        );

        InjectingRecipe recipe = new InjectingRecipe(LucenticsRecipeTypesRegister.INJECTING_INFO, args);
        output.accept(id, recipe, null);
    }
}
