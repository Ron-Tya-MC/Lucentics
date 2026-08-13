package io.github.rontyamc.lucentics.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public record RecipeInfo<I extends RecipeInput, R extends Recipe<I>>(
        ResourceLocation id,
        RecipeType<R> type,
        RecipeSerializer<R> serializer
) implements IRecipeInfo {
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I2 extends RecipeInput, R2 extends Recipe<I2>> RecipeType<R2> getType() {
        return (RecipeType<R2>) type;
    }
}
