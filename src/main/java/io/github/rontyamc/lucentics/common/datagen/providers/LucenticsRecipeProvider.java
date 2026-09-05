package io.github.rontyamc.lucentics.common.datagen.providers;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.datagen.builders.CrushingBuilder;
import io.github.rontyamc.lucentics.common.util.ItemUtilities;
import io.github.rontyamc.lucentics.common.datagen.builders.InjectingBuilder;
import io.github.rontyamc.lucentics.common.datagen.builders.TrailRecipeBuilder;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class LucenticsRecipeProvider extends RecipeProvider {
    /*
     * Derived from Create:
     * https://github.com/Creators-of-Create/Create
     *
     * Copyright (c) The Create Team / The Creators of Create
     * Licensed under the MIT License.
     */
    protected final List<GeneratedRecipe> generatedRecipes = new ArrayList<>();

    public LucenticsRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        CraftingProvider.buildRecipes(this, recipeOutput);
        InjectingProvider.buildRecipes(this, recipeOutput);
        EngravingProvider.buildRecipes(this, recipeOutput);
        CrushingProvider.buildRecipes(this, recipeOutput);
        MillingProvider.buildRecipes(this, recipeOutput);

        generatedRecipes.forEach(c -> c.register(recipeOutput));
    }

    protected GeneratedRecipeBuilder generic(Supplier<? extends ItemLike> result, int count) {
        return new GeneratedRecipeBuilder(result, count);
    }

    protected GeneratedRecipeBuilder generic(Supplier<? extends ItemLike> result) {
        return generic(result, 1);
    }

    String currentFolder = "";

    void enterFolder(String folder) {
        currentFolder = folder;
        Lucentics.LOGGER.debug("entered:{}", currentFolder);
    }

    void leaveFolder() {
        Lucentics.LOGGER.debug("leaved:{}", currentFolder);
        currentFolder = "";
    }

    @FunctionalInterface
    public interface GeneratedRecipe {
        void register(RecipeOutput recipeOutput);
    }

    public class GeneratedRecipeBuilder {
        protected final Supplier<? extends ItemLike> result;
        protected final int count;
        protected String folder;
        protected String suffix;
        @Nullable
        protected Supplier<? extends ItemLike> unlockedBy;

        protected GeneratedRecipeBuilder(Supplier<? extends ItemLike> result, int count) {
            this.result = result;
            this.count = count;
            this.folder = currentFolder;
            this.suffix = "";
        }

        public GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemLike> item) {
            this.unlockedBy = item;
            return this;
        }

        public GeneratedRecipeBuilder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        protected ResourceLocation createLocation(String category) {
            ResourceLocation id = ItemUtilities.getId(result);
            return folder.isEmpty() ? Lucentics.defaultLocation(category + "/" + id.getPath() + suffix) : Lucentics.defaultLocation(category + "/" + folder + "/" + id.getPath() + suffix);
        }

        protected GeneratedRecipe register(Consumer<RecipeOutput> callback) {
            GeneratedRecipe recipe = callback::accept;
            generatedRecipes.add(recipe);
            return recipe;
        }

        public GeneratedRecipe shaped(UnaryOperator<ShapedRecipeBuilder> builder) {
            return register(output -> {
                ShapedRecipeBuilder b =
                        builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get(), count));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", has(unlockedBy.get()));
                b.save(output, createLocation("crafting"));
            });
        }

        public GeneratedRecipe shapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return register(output -> {
                ShapelessRecipeBuilder b =
                        builder.apply(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get(), count));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", has(unlockedBy.get()));
                b.save(output, createLocation("crafting"));
            });
        }

        public GeneratedRecipe injecting(UnaryOperator<InjectingBuilder> builder) {
            return register(output -> {
                InjectingBuilder b =
                        builder.apply(InjectingBuilder.create(SizedIngredient.EMPTY, result.get(), count));
                b.save(output, createLocation("injecting"));
            });
        }

        public GeneratedRecipe engraving(UnaryOperator<TrailRecipeBuilder> builder) {
            return register(output -> {
                TrailRecipeBuilder b =
                        builder.apply(TrailRecipeBuilder.create(SizedIngredient.EMPTY, result.get(), count));
                b.setFolder("engraving");
                b.setRecipeInfo(LucenticsRecipeTypesRegister.ENGRAVING_INFO);
                b.save(output, createLocation("engraving"));
            });
        }

        public GeneratedRecipe crushing(UnaryOperator<CrushingBuilder> builder) {
            return register(output -> {
                CrushingBuilder b =
                        builder.apply(CrushingBuilder.create(Ingredient.EMPTY, result.get()));
                b.save(output, createLocation("crushing"));
            });
        }

        public GeneratedRecipe milling(UnaryOperator<TrailRecipeBuilder> builder) {
            return register(output -> {
                TrailRecipeBuilder b =
                        builder.apply(TrailRecipeBuilder.create(SizedIngredient.EMPTY, result.get(), count));
                b.setFolder("milling");
                b.setRecipeInfo(LucenticsRecipeTypesRegister.MILLING_INFO);
                b.save(output, createLocation("milling"));
            });
        }
    }
}
