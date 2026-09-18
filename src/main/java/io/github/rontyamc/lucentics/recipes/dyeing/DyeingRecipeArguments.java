package io.github.rontyamc.lucentics.recipes.dyeing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record DyeingRecipeArguments(Ingredient input, Colors color, int liquidAmount, ItemStack output) {
    public DyeingRecipeArguments() {
        this(Ingredient.EMPTY, Colors.SUNLIGHT, 0, ItemStack.EMPTY);
    }

    public static final MapCodec<DyeingRecipeArguments> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Ingredient.CODEC.fieldOf("input").forGetter(DyeingRecipeArguments::input),
            Colors.CODEC.fieldOf("color").forGetter(DyeingRecipeArguments::color),
            Codec.INT.fieldOf("required_mb").forGetter(DyeingRecipeArguments::liquidAmount),
            ItemStack.CODEC.fieldOf("output").forGetter(DyeingRecipeArguments::output)
    ).apply(ins, DyeingRecipeArguments::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DyeingRecipeArguments> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, DyeingRecipeArguments::input,
            Colors.STREAM_CODEC.cast(), DyeingRecipeArguments::color,
            ByteBufCodecs.VAR_INT, DyeingRecipeArguments::liquidAmount,
            ItemStack.STREAM_CODEC, DyeingRecipeArguments::output,
            DyeingRecipeArguments::new
    );
}
