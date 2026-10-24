package io.github.rontyamc.lucentics.recipes.dyeing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public record DyeingRecipeArguments(Ingredient input, SizedFluidIngredient fluid, ItemStack output, int processingDuration) {
    public DyeingRecipeArguments() {
        this(Ingredient.EMPTY, new SizedFluidIngredient(FluidIngredient.empty(), 1), ItemStack.EMPTY, 0);
    }

    public static final MapCodec<DyeingRecipeArguments> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Ingredient.CODEC.fieldOf("input").forGetter(DyeingRecipeArguments::input),
            SizedFluidIngredient.FLAT_CODEC.fieldOf("color").forGetter(DyeingRecipeArguments::fluid),
            ItemStack.CODEC.fieldOf("output").forGetter(DyeingRecipeArguments::output),
            Codec.INT.fieldOf("processing_duration").forGetter(DyeingRecipeArguments::processingDuration)
    ).apply(ins, DyeingRecipeArguments::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DyeingRecipeArguments> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, DyeingRecipeArguments::input,
            SizedFluidIngredient.STREAM_CODEC, DyeingRecipeArguments::fluid,
            ItemStack.STREAM_CODEC, DyeingRecipeArguments::output,
            ByteBufCodecs.VAR_INT, DyeingRecipeArguments::processingDuration,
            DyeingRecipeArguments::new
    );
}
