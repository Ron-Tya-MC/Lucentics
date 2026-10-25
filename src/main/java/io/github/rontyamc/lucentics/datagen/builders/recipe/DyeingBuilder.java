package io.github.rontyamc.lucentics.datagen.builders.recipe;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.recipes.dyeing.DyeingRecipe;
import io.github.rontyamc.lucentics.recipes.dyeing.DyeingRecipeArguments;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.function.Supplier;

public class DyeingBuilder implements RecipeBuilder {
    Ingredient item;
    SizedFluidIngredient fluid;
    ItemStack output;
    int processingDuration = 15;
    protected String suffix;

    public DyeingBuilder(Ingredient item, SizedFluidIngredient fluid, ItemStack output) {
        this.item = item;
        this.fluid = fluid;
        this.output = output;
        this.suffix = "";
    }

    public static DyeingBuilder create(Ingredient item, SizedFluidIngredient fluid, ItemStack output) {
        return new DyeingBuilder(item, fluid, output);
    }

    public static DyeingBuilder create(Ingredient item, FluidIngredient fluid, ItemStack output) {
        return create(item, new SizedFluidIngredient(fluid, FluidType.BUCKET_VOLUME), output);
    }

    public static DyeingBuilder create(Ingredient item, Fluid fluid, ItemStack output) {
        return create(item, FluidIngredient.of(fluid), output);
    }

    public static DyeingBuilder create(ItemStack output) {
        return create(Ingredient.EMPTY, FluidIngredient.empty(), output);
    }

    public static DyeingBuilder create(ItemLike output) {
        return create(Ingredient.EMPTY, FluidIngredient.empty(), new ItemStack(output));
    }

    public DyeingBuilder item(Supplier<? extends ItemLike> item) {
        this.item = Ingredient.of(item.get());
        return this;
    }

    public DyeingBuilder item(Ingredient item) {
        this.item = item;
        return this;
    }

    public DyeingBuilder fluid(FluidStack fluid) {
        this.fluid = SizedFluidIngredient.of(fluid);
        return this;
    }

    public DyeingBuilder fluid(Fluid fluid, int amount) {
        this.fluid = SizedFluidIngredient.of(new FluidStack(fluid, amount));
        return this;
    }

    public DyeingBuilder fluid(Fluid fluid) {
        this.fluid = SizedFluidIngredient.of(new FluidStack(fluid, FluidType.BUCKET_VOLUME));
        return this;
    }

    public DyeingBuilder fluidAmount(int fluidAmount) {
        this.fluid = new SizedFluidIngredient(this.fluid.ingredient(), fluidAmount);
        return this;
    }

    public DyeingBuilder duration(int duration) {
        this.processingDuration = duration;
        return this;
    }

    @Override
    public DyeingBuilder unlockedBy(String criterionName, Criterion<?> criterion) {
        return this;
    }

    @Override
    public DyeingBuilder group(String groupName) {
        return this;
    }

    public DyeingBuilder suffix(String suffix) {
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
        ResourceLocation id = Lucentics.path("dyeing/" + defaultId.getPath() + suffix);
        save(output, id);
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        DyeingRecipeArguments args = new DyeingRecipeArguments(
                item,
                fluid,
                this.output,
                processingDuration
        );

        DyeingRecipe recipe = new DyeingRecipe(LucenticsRecipeTypesRegister.DYEING_INFO, args);
        output.accept(id, recipe, null);
    }
}
