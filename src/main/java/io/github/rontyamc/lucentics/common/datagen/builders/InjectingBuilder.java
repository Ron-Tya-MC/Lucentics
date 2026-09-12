package io.github.rontyamc.lucentics.common.datagen.builders;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments.WeightedOutput;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import io.github.rontyamc.lucentics.common.recipe.SizedThingIngredient;
import io.github.rontyamc.lucentics.recipes.injecting.InjectingRecipe;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class InjectingBuilder implements RecipeBuilder, IdPathResolvable {
    private SizedThingIngredient input;
    private final ItemStack primaryOutput;
    private final List<List<WeightedOutput>> outputGroups = NonNullList.create();
    protected String suffix;
    private int processingDuration = 100;
    private int daylightCondition = 0;

    public InjectingBuilder(SizedThingIngredient input, ItemStack output) {
        this.input = input;
        this.primaryOutput = output;
        if (!output.isEmpty()) this.outputGroups.add(List.of(OutputSpec.of(output).build()));
        this.suffix = "";
    }

    public static InjectingBuilder create(SizedThingIngredient input, ItemStack result) {
        return new InjectingBuilder(input, result);
    }

    public static InjectingBuilder create(SizedThingIngredient input, ItemLike result, int count) {
        return create(input, new ItemStack(result, count));
    }
    public static InjectingBuilder create(SizedThingIngredient input, ItemLike result) {
        return create(input, result, 1);
    }

    public InjectingBuilder input(SizedThingIngredient input) {
        this.input = input;
        return this;
    }

    public InjectingBuilder input(SizedIngredient input) {
        this.input = SizedThingIngredient.of(input);
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

    public InjectingBuilder output(OutputSpec spec) {
        outputGroups.add(List.of(spec.build()));
        return this;
    }

    public InjectingBuilder outputGroup(OutputSpec... specs) {
        outputGroups.add(Arrays.stream(specs).map(OutputSpec::build).toList());
        return this;
    }

    public InjectingBuilder output(ItemLike item) {
        return output(OutputSpec.of(item));
    }

    public InjectingBuilder output(ItemLike item, int count) {
        return output(OutputSpec.of(item).amount(count));
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
        return primaryOutput.getItem();
    }

    @Override
    public String resolveIdPath() {
        if (!primaryOutput.isEmpty()) {
            return BuiltInRegistries.ITEM.getKey(primaryOutput.getItem()).getPath();
        }
        for (List<WeightedOutput> group : outputGroups) {
            for (WeightedOutput candidate : group) {
                Optional<String> path = candidate.content().left()
                        .filter(item -> !item.stack().isEmpty())
                        .map(item -> BuiltInRegistries.ITEM.getKey(item.stack().getItem()).getPath())
                        .or(() -> candidate.content().right()
                                .map(fluid -> BuiltInRegistries.FLUID.getKey(fluid.stack().getFluid()).getPath()));
                if (path.isPresent()) return path.get();
            }
        }
        throw new IllegalStateException("Cannot resolve recipe id: no non-empty item or fluid output found. Specify path(String path) explicitly.");
    }

    @Override
    public void save(RecipeOutput output) {
        ResourceLocation defaultId = RecipeBuilder.getDefaultRecipeId(getResult());
        ResourceLocation id = Lucentics.defaultLocation("injecting/" + defaultId.getPath() + suffix);
        save(output, id);
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        NonNullList<List<WeightedOutput>> outputs = NonNullList.create();
        outputs.addAll(outputGroups);

        RecipeArguments args = new RecipeArguments(
                input,
                NonNullList.create(),
                outputs,
                processingDuration,
                daylightCondition
        );

        InjectingRecipe recipe = new InjectingRecipe(LucenticsRecipeTypesRegister.INJECTING_INFO, args);
        output.accept(id, recipe, null);
    }
}
