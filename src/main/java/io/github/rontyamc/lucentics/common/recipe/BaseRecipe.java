package io.github.rontyamc.lucentics.common.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Optional;

public abstract class BaseRecipe<I extends RecipeInput, A extends RecipeArguments> implements Recipe<I> {
    protected A arguments;
    protected Optional<SizedIngredient> mainInput;
    protected Optional<SizedFluidIngredient> mainFluidInput;
    protected NonNullList<RecipeArguments.TrailInput> trailInputs;
    protected ItemStack output;
    protected FluidStack outputFluid;
    protected int processingDuration;
    protected int dayLightCondition;

    protected RecipeType<?> type;
    protected RecipeSerializer<?> serializer;
    protected IRecipeInfo recipeInfo;

    public BaseRecipe(IRecipeInfo recipeInfo, A args) {
        this.arguments = args;
        this.mainInput = args.mainInput();
        this.mainFluidInput = args.mainFluidInput();
        this.trailInputs = args.trailInputs();
        this.output = args.output();
        this.outputFluid = args.outputFluid();
        this.processingDuration = args.processingDuration();
        this.dayLightCondition = args.dayLightCondition();

        this.type = recipeInfo.getType();
        this.serializer = recipeInfo.getSerializer();
        this.recipeInfo = recipeInfo;
    }

    protected abstract int getMaxInputItem();

    protected abstract int getMaxOutputItem();
}

