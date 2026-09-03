package io.github.rontyamc.lucentics.recipes.crushing;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.BiFunction;

public class CrushingRecipeSerializer<R extends CrushingRecipe> implements RecipeSerializer<R> {
    private final MapCodec<R> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

    public CrushingRecipeSerializer(BiFunction<IRecipeInfo, CrushingRecipeArguments, R> factory, IRecipeInfo info) {
        this.codec = CrushingRecipeArguments.CODEC.xmap(
                args -> factory.apply(info, args),
                recipe -> recipe.arguments
        );
        this.streamCodec = CrushingRecipeArguments.STREAM_CODEC.map(
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
