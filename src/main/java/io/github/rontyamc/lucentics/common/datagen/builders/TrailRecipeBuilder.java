package io.github.rontyamc.lucentics.common.datagen.builders;

import com.mojang.datafixers.util.Either;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments.TrailInput;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments.WeightedOutput;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TrailRecipeBuilder implements RecipeBuilder, IdPathResolvable {
    private SizedIngredient input;
    private final ItemStack primaryOutput;
    private final List<List<WeightedOutput>> outputGroups = NonNullList.create();
    private final NonNullList<TrailInput> trails = NonNullList.create();
    protected String folder;
    protected String suffix;
    protected IRecipeInfo recipeInfo;
    private int processingDuration = 100;
    private int daylightCondition = 0;

    protected TrailRecipeBuilder(SizedIngredient input, ItemStack output) {
        this.input = input;
        this.primaryOutput = output;
        if (!output.isEmpty()) this.outputGroups.add(List.of(OutputSpec.of(output).build()));
        this.folder = "";
        this.suffix = "";
    }

    public static TrailRecipeBuilder create(SizedIngredient input, ItemStack output) {
        return new TrailRecipeBuilder(input, output);
    }

    public static TrailRecipeBuilder create(SizedIngredient input, ItemLike result, int count) {
        return new TrailRecipeBuilder(input, new ItemStack(result, count));
    }

    public static TrailRecipeBuilder create(SizedIngredient input, ItemLike result) {
        return new TrailRecipeBuilder(input, new ItemStack(result, 1));
    }

    public TrailRecipeBuilder input(SizedIngredient input) {
        this.input = input;
        return this;
    }

    public TrailRecipeBuilder input(ItemLike input, int count) {
        return input(SizedIngredient.of(input, count));
    }

    public TrailRecipeBuilder input(ItemLike input) {
        return input(input, 1);
    }

    public TrailRecipeBuilder trail(TrailBuilder trailBuilder) {
        trails.add(trailBuilder.build());
        return this;
    }

    public TrailRecipeBuilder duration(int ticks) {
        this.processingDuration = ticks;
        return this;
    }

    public TrailRecipeBuilder daylight(int level) {
        this.daylightCondition = level;
        return this;
    }

    public TrailRecipeBuilder output(OutputSpec spec) {
        outputGroups.add(List.of(spec.build()));
        return this;
    }

    public TrailRecipeBuilder outputGroup(OutputSpec... specs) {
        outputGroups.add(Arrays.stream(specs).map(OutputSpec::build).toList());
        return this;
    }

    public TrailRecipeBuilder output(ItemLike item) {
        return output(OutputSpec.of(item));
    }

    public TrailRecipeBuilder output(ItemLike item, int count) {
        return output(OutputSpec.of(item).amount(count));
    }

    public TrailRecipeBuilder output(Fluid fluid, int amount) {
        return output(OutputSpec.of(fluid).amount(amount));
    }

    @Override
    public TrailRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterion) {
        return this;
    }

    @Override
    public TrailRecipeBuilder group(String groupName) {
        return this;
    }

    public TrailRecipeBuilder suffix(String suffix) {
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
        ResourceLocation id = folder.isEmpty() ? Lucentics.defaultLocation(defaultId.getPath() + suffix) : Lucentics.defaultLocation(folder + "/" + defaultId.getPath() + suffix);
        save(output, id);
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        if (recipeInfo == null) throw new IllegalArgumentException("Recipe info of the recipe \"" + id + "\" cannot be null");

        NonNullList<List<WeightedOutput>> outputs = NonNullList.create();
        outputs.addAll(outputGroups);

        RecipeArguments args = new RecipeArguments(
                Either.left(input),
                trails,
                outputs,
                processingDuration,
                daylightCondition
        );

        TrailRecipe recipe = new TrailRecipe(recipeInfo, args);
        output.accept(id, recipe, null);
    }

    public void setFolder(String folder) {
        this.folder = folder;
        Lucentics.LOGGER.debug("set:{}", this.folder);
    }

    public void setRecipeInfo(IRecipeInfo recipeInfo) {
        this.recipeInfo = recipeInfo;
    }
}
