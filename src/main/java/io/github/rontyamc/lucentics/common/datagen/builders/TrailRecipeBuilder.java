package io.github.rontyamc.lucentics.common.datagen.builders;

import com.mojang.datafixers.util.Either;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

public class TrailRecipeBuilder implements RecipeBuilder {
    private SizedIngredient input;
    private final ItemStack output;
    private final NonNullList<RecipeArguments.TrailInput> trails = NonNullList.create();
    protected String folder;
    protected String suffix;
    protected IRecipeInfo recipeInfo;
    private int processingDuration = 100;
    private int daylightCondition = 0;

    protected TrailRecipeBuilder(SizedIngredient input, ItemStack output) {
        this.input = input;
        this.output = output;
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
        return output.getItem();
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

        NonNullList<RecipeArguments.Output> outputs = NonNullList.create();
        outputs.add(new RecipeArguments.Output(Optional.of(this.output), Optional.empty()));

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
