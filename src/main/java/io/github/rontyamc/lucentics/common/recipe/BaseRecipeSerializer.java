package io.github.rontyamc.lucentics.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.BiFunction;

public class BaseRecipeSerializer<I extends RecipeInput, R extends BaseRecipe<I, RecipeArguments>> implements RecipeSerializer<R> {

    private final MapCodec<R> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

    public BaseRecipeSerializer(BiFunction<IRecipeInfo, RecipeArguments, R> factory, IRecipeInfo info) {
        this.codec = RecipeArguments.CODEC.xmap(
                args -> factory.apply(info, args),
                recipe -> recipe.arguments
        );
        this.streamCodec = RecipeArguments.STREAM_CODEC.map(
                args -> factory.apply(info, args),
                recipe -> recipe.arguments
        );
    }

    @Override
    public MapCodec<R> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
        return streamCodec;
    }
}