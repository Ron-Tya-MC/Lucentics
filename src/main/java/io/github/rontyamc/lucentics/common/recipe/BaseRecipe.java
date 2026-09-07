package io.github.rontyamc.lucentics.common.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;
import java.util.Optional;

public abstract class BaseRecipe<I extends RecipeInput, A extends RecipeArguments> implements Recipe<I> {
    protected A arguments;
    protected Optional<SizedIngredient> mainInput;
    protected Optional<SizedFluidIngredient> mainFluidInput;
    protected NonNullList<RecipeArguments.TrailInput> trailInputs;
    protected NonNullList<List<RecipeArguments.WeightedOutput>> outputs;
    protected int processingDuration;
    protected int dayLightCondition;

    protected RecipeType<?> type;
    protected RecipeSerializer<?> serializer;
    protected IRecipeInfo recipeInfo;

    public BaseRecipe(IRecipeInfo recipeInfo, A args) {
        this.arguments = args;
        this.mainInput = args.mainInput().left();
        this.mainFluidInput = args.mainInput().right();
        this.trailInputs = args.trailInputs();
        this.outputs = args.outputs();
        this.processingDuration = args.processingDuration();
        this.dayLightCondition = args.dayLightCondition();

        this.type = recipeInfo.getType();
        this.serializer = recipeInfo.getSerializer();
        this.recipeInfo = recipeInfo;
    }

    public A getArguments() {
        return arguments;
    }

    public Optional<SizedIngredient> getMainInput() {
        return mainInput;
    }

    public Optional<SizedFluidIngredient> getMainFluidInput() {
        return mainFluidInput;
    }

    public NonNullList<RecipeArguments.TrailInput> getTrailInputs() {
        return trailInputs;
    }

    public NonNullList<List<RecipeArguments.WeightedOutput>> getOutputs() {
        return outputs;
    }


    public int getProcessingDuration() {
        return processingDuration;
    }

    public int getDayLightCondition() {
        return dayLightCondition;
    }

    public IRecipeInfo getRecipeInfo() {
        return recipeInfo;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        mainInput.ifPresent(sized -> list.add(sized.ingredient()));
        for (RecipeArguments.TrailInput trail : trailInputs) {
            for (RecipeArguments.OrderingInput ordering : trail.inputs()) {
                ordering.ingredient().left().ifPresent(sized -> list.add(sized.ingredient()));
            }
        }
        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        for (var group : outputs) {
            for (var candidate : group) {
                Optional<ItemStack> item = candidate.content().left()
                        .map(RecipeArguments.WeightedOutput.ItemOutput::stack)
                        .filter(stack -> !stack.isEmpty());
                if (item.isPresent()) return item.get();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack assemble(I input, HolderLookup.Provider registries) {
        return getResultItem(registries).copy();
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    @Override
    public abstract boolean matches(I input, Level level);

    @Override
    public boolean isSpecial() {
        return true;
    }
}

