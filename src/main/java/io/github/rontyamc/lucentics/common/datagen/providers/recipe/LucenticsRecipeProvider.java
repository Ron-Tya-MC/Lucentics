package io.github.rontyamc.lucentics.common.datagen.providers.recipe;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.datagen.builders.*;
import io.github.rontyamc.lucentics.common.recipe.SizedThingIngredient;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import io.github.rontyamc.lucentics.common.util.MiscUtil;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
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
        SmeltingProvider.buildRecipes(this, recipeOutput);
        InjectingProvider.buildRecipes(this, recipeOutput);
        EngravingProvider.buildRecipes(this, recipeOutput);
        CrushingProvider.buildRecipes(this, recipeOutput);
        MillingProvider.buildRecipes(this, recipeOutput);
        MixingProvider.buildRecipes(this, recipeOutput);
        AssemblingProvider.buildRecipes(this, recipeOutput);
        DyeingProvider.buildRecipes(this, recipeOutput);

        generatedRecipes.forEach(c -> c.register(recipeOutput));
    }

    protected GeneratedRecipeBuilder generic(Supplier<? extends ItemLike> result, int count) {
        return new GeneratedRecipeBuilder(result, count);
    }

    protected GeneratedRecipeBuilder generic(Supplier<? extends ItemLike> result) {
        return generic(result, 1);
    }

    protected GeneratedRecipeBuilder generic() {
        return generic(() -> Items.AIR, 1);
    }

    String currentFolder = "";

    void enterFolder(String folder) {
        currentFolder = folder;
    }

    void leaveFolder() {
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
        protected String path;
        protected String suffix;
        @Nullable
        protected Supplier<ItemPredicate> unlockedBy;

        protected GeneratedRecipeBuilder(Supplier<? extends ItemLike> result, int count) {
            this.result = result;
            this.count = count;
            this.folder = currentFolder;
            this.path = "";
            this.suffix = "";

            unlockedBy(LucenticsItemRegister.NOTHING);
        }

        GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemLike> item) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(item.get())
                    .build();
            return this;
        }

        GeneratedRecipeBuilder unlockedByTag(Supplier<TagKey<Item>> tag) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(tag.get())
                    .build();
            return this;
        }

        public GeneratedRecipeBuilder path(String path) {
            this.path = path;
            return this;
        }

        public GeneratedRecipeBuilder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        protected ResourceLocation createLocation(String category) {
            if (path.isEmpty()) path = ItemUtil.getId(result).getPath();
            return folder.isEmpty()
                    ? Lucentics.defaultLocation(category + "/" + path + suffix)
                    : Lucentics.defaultLocation(category + "/" + folder + "/" + path + suffix);
        }

        protected ResourceLocation createLocation(String category, IdPathResolvable b) {
            if (path.isEmpty()) path = result.get() == Items.AIR ? b.resolveIdPath() : ItemUtil.getId(result).getPath();
            return folder.isEmpty()
                    ? Lucentics.defaultLocation(category + "/" + path + suffix)
                    : Lucentics.defaultLocation(category + "/" + folder + "/" + path + suffix);
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
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(output, createLocation("crafting"));
            });
        }

        public GeneratedRecipe shapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return register(output -> {
                ShapelessRecipeBuilder b =
                        builder.apply(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get(), count));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(output, createLocation("crafting"));
            });
        }

        public GeneratedRecipe injecting(UnaryOperator<InjectingBuilder> builder) {
            return register(output -> {
                InjectingBuilder b =
                        builder.apply(InjectingBuilder.create(SizedThingIngredient.EMPTY, result.get(), count));
                b.save(output, createLocation("injecting", b));
            });
        }

        public GeneratedRecipe engraving(UnaryOperator<TrailRecipeBuilder> builder) {
            return register(output -> {
                TrailRecipeBuilder b =
                        builder.apply(TrailRecipeBuilder.create(SizedThingIngredient.EMPTY, result.get(), count));
                b.setFolder("engraving");
                b.setRecipeInfo(LucenticsRecipeTypesRegister.ENGRAVING_INFO);
                b.save(output, createLocation("engraving", b));
            });
        }

        public GeneratedRecipe crushing(UnaryOperator<CrushingBuilder> builder) {
            return register(output -> {
                CrushingBuilder b =
                        builder.apply(CrushingBuilder.create(Ingredient.EMPTY, result.get()));
                b.save(output, createLocation("crushing", b));
            });
        }

        public GeneratedRecipe milling(UnaryOperator<TrailRecipeBuilder> builder) {
            return register(output -> {
                TrailRecipeBuilder b =
                        builder.apply(TrailRecipeBuilder.create(SizedThingIngredient.EMPTY, result.get(), count));
                b.setFolder("milling");
                b.setRecipeInfo(LucenticsRecipeTypesRegister.MILLING_INFO);
                b.save(output, createLocation("milling", b));
            });
        }

        public GeneratedRecipe mixing(UnaryOperator<TrailRecipeBuilder> builder) {
            return register(output -> {
                TrailRecipeBuilder b =
                        builder.apply(TrailRecipeBuilder.create(SizedThingIngredient.EMPTY, result.get(), count));
                b.setFolder("mixing");
                b.setRecipeInfo(LucenticsRecipeTypesRegister.MIXING_INFO);
                b.save(output, createLocation("mixing", b));
            });
        }

        public GeneratedRecipe assembling(UnaryOperator<TrailRecipeBuilder> builder) {
            return register(output -> {
                TrailRecipeBuilder b =
                        builder.apply(TrailRecipeBuilder.create(SizedThingIngredient.EMPTY, result.get(), count));
                b.setFolder("assembling");
                b.setRecipeInfo(LucenticsRecipeTypesRegister.ASSEMBLING_INFO);
                b.save(output, createLocation("assembling", b));
            });
        }

        public GeneratedRecipe dyeing(UnaryOperator<DyeingBuilder> builder) {
            return register(output -> {
                DyeingBuilder b =
                        builder.apply(DyeingBuilder.create(result.get()));
                b.save(output, createLocation("dyeing"));
            });
        }

        GeneratedCookingRecipeBuilder cooking(Supplier<? extends ItemLike> item) {
            return unlockedBy(item).cookingIngredient(() -> Ingredient.of(item.get()));
        }

        GeneratedCookingRecipeBuilder cookingTag(Supplier<TagKey<Item>> tag) {
            return unlockedByTag(tag).cookingIngredient(() -> Ingredient.of(tag.get()));
        }

        GeneratedCookingRecipeBuilder cookingIngredient(Supplier<Ingredient> ingredient) {
            return new GeneratedCookingRecipeBuilder(ingredient);
        }


        class GeneratedCookingRecipeBuilder {
            private Supplier<Ingredient> ingredient;
            private float exp;
            private int cookingTime;

            GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
                this.ingredient = ingredient;
                cookingTime = 200;
                exp = 0;
            }

            private <T extends AbstractCookingRecipe> GeneratedRecipe create(RecipeSerializer<T> serializer,
                                                                             UnaryOperator<SimpleCookingRecipeBuilder> builder, AbstractCookingRecipe.Factory<T> factory, float cookingTimeModifier) {
                return register(output -> {

                    SimpleCookingRecipeBuilder b = builder.apply(SimpleCookingRecipeBuilder.generic(ingredient.get(),
                            RecipeCategory.MISC, result.get(), exp,
                            (int) (cookingTime * cookingTimeModifier), serializer, factory));
                    if (unlockedBy != null)
                        b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));

                    b.save(output, createLocation(MiscUtil.getKeyOrThrow(serializer).getPath()));
                });
            }

            GeneratedCookingRecipeBuilder cookingTime(int duration) {
                cookingTime = duration;
                return this;
            }

            GeneratedCookingRecipeBuilder exp(float xp) {
                exp = xp;
                return this;
            }

            GeneratedRecipe inFurnace() {
                return inFurnace(b -> b);
            }

            GeneratedRecipe inFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                return create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
            }

            GeneratedRecipe inSmoker() {
                return inSmoker(b -> b);
            }

            GeneratedRecipe inSmoker(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
                create(RecipeSerializer.CAMPFIRE_COOKING_RECIPE, builder, CampfireCookingRecipe::new, 3);
                return create(RecipeSerializer.SMOKING_RECIPE, builder, SmokingRecipe::new, .5f);
            }

            GeneratedRecipe inBlastFurnace() {
                return inBlastFurnace(b -> b);
            }

            GeneratedRecipe inBlastFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
                return create(RecipeSerializer.BLASTING_RECIPE, builder, BlastingRecipe::new, .5f);
            }
        }
    }
}
