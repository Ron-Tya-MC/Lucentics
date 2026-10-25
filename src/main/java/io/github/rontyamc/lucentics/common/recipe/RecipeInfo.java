package io.github.rontyamc.lucentics.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public record RecipeInfo<I extends RecipeInput, R extends Recipe<I>>(
        ResourceLocation id,
        Supplier<RecipeType<R>> type,
        Supplier<RecipeSerializer<R>> serializer
) implements IRecipeInfo {
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializer.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I_ extends RecipeInput, R_ extends Recipe<I_>> RecipeType<R_> getType() {
        return (RecipeType<R_>) type.get();
    }
}
