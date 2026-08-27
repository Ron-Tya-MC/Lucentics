package io.github.rontyamc.lucentics.common.datagen.providers;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.ItemUtilities;
import io.github.rontyamc.lucentics.common.datagen.builders.InjectingBuilder;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    /*
     * Derived from Create:
     * https://github.com/Creators-of-Create/Create
     *
     * Copyright (c) The Create Team / The Creators of Create
     * Licensed under the MIT License.
     */
    protected final List<GeneratedRecipe> generatedRecipes = new ArrayList<>();

    public RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        CraftingProvider.buildRecipes(this, recipeOutput);
        InjectingProvider.buildRecipes(this, recipeOutput);

        generatedRecipes.forEach(c -> c.register(recipeOutput));
    }

    protected GeneratedRecipeBuilder generic(Supplier<? extends ItemLike> result, int amount) {
        return new GeneratedRecipeBuilder(result, amount);
    }

    protected GeneratedRecipeBuilder generic(Supplier<? extends ItemLike> result) {
        return generic(result, 1);
    }

    String currentFolder = "";

    void enterFolder(String folder) {
        currentFolder = folder;
        Lucentics.LOGGER.info("entered:{}", currentFolder);
    }

    void leaveFolder() {
        Lucentics.LOGGER.info("leaved:{}", currentFolder);
        currentFolder = "";
    }

    @FunctionalInterface
    public interface GeneratedRecipe {
        void register(RecipeOutput recipeOutput);
    }

    public class GeneratedRecipeBuilder {
        protected final Supplier<? extends ItemLike> result;
        protected final int amount;
        protected String folder;
        @Nullable
        protected Supplier<? extends ItemLike> unlockedBy;

        protected GeneratedRecipeBuilder(Supplier<? extends ItemLike> result, int amount) {
            this.result = result;
            this.amount = amount;
            this.folder = currentFolder;
        }

        public GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemLike> item) {
            this.unlockedBy = item;
            return this;
        }

        protected ResourceLocation createLocation(String category) {
            ResourceLocation id = ItemUtilities.getId(result);
            return folder.isEmpty() ? Lucentics.defaultLocation(category + "/" + id.getPath()) : Lucentics.defaultLocation(category + "/" + folder + "/" + id.getPath());
        }

        protected GeneratedRecipe register(Consumer<RecipeOutput> callback) {
            GeneratedRecipe recipe = callback::accept;
            generatedRecipes.add(recipe);
            return recipe;
        }

        public GeneratedRecipe shaped(UnaryOperator<ShapedRecipeBuilder> builder) {
            return register(output -> {
                ShapedRecipeBuilder b =
                        builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", has(unlockedBy.get()));
                b.save(output, createLocation("crafting"));
            });
        }

        public GeneratedRecipe injecting(UnaryOperator<InjectingBuilder> builder) {
            return register(output -> {
                InjectingBuilder b =
                        builder.apply(InjectingBuilder.create(SizedIngredient.EMPTY, result.get(), amount));
                b.save(output, createLocation("injecting"));
            });
        }
    }
}
