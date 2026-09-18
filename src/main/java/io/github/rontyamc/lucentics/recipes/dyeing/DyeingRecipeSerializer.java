package io.github.rontyamc.lucentics.recipes.dyeing;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.BiFunction;

public class DyeingRecipeSerializer implements RecipeSerializer<DyeingRecipe> {
    private final MapCodec<DyeingRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, DyeingRecipe> streamCodec;

    public DyeingRecipeSerializer(BiFunction<IRecipeInfo, DyeingRecipeArguments, DyeingRecipe> factory, IRecipeInfo info) {
        this.codec = DyeingRecipeArguments.CODEC.xmap(
                args -> factory.apply(info, args),
                recipe -> recipe.arguments
        );
        this.streamCodec = DyeingRecipeArguments.STREAM_CODEC.map(
                args -> factory.apply(info, args),
                recipe -> recipe.arguments
        );
    }

    @Override
    public MapCodec<DyeingRecipe> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DyeingRecipe> streamCodec() {
        return streamCodec;
    }
}
