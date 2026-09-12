package io.github.rontyamc.lucentics.common.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.github.rontyamc.lucentics.common.ThingStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Optional;

public record SizedThingIngredient(Either<SizedIngredient, SizedFluidIngredient> content) {
    public static final SizedThingIngredient EMPTY = new SizedThingIngredient(Either.left(SizedIngredient.EMPTY));

    public static SizedThingIngredient of(ThingStack stack) {
        return new SizedThingIngredient(stack.content().map(
                item -> Either.left(SizedIngredient.of(item)), fluid -> Either.right(SizedFluidIngredient.of(fluid))
        ));
    }

    public static SizedThingIngredient of(ItemStack stack) {
        return new SizedThingIngredient(Either.left(SizedIngredient.of(stack)));
    }

    public static SizedThingIngredient of(SizedIngredient ingredient) {
        return new SizedThingIngredient(Either.left(ingredient));
    }

    public static SizedThingIngredient of(FluidStack stack) {
        return new SizedThingIngredient(Either.right(SizedFluidIngredient.of(stack)));
    }

    public static SizedThingIngredient of(SizedFluidIngredient ingredient) {
        return new SizedThingIngredient(Either.right(ingredient));
    }

    public Optional<SizedIngredient> asItem() {
        return content.left();
    }

    public Optional<SizedFluidIngredient> asFluid() {
        return content.right();
    }

    public boolean isItem() {
        return content.left().isPresent();
    }

    public boolean isFluid() {
        return content.right().isPresent();
    }

    public int amount() {
        return content.map(SizedIngredient::count, SizedFluidIngredient::amount);
    }

    // CountやAmountまで一致しているかを見る。少し注意(一敗)
    public boolean matches(SizedThingIngredient ingredient) {
        if (ingredient.isItem()) {
            return content.left().isPresent() && content.left().get().equals(ingredient.asItem().get());
        }
        else {
            return content.right().isPresent() && content.right().get().equals(ingredient.asFluid().get());
        }
    }

    public boolean test(ThingStack stack) {
        if (stack.isItem()) {
            return content.left().isPresent() && content.left().get().test(stack.asItemOrEmpty());
        }
        else {
            return content.right().isPresent() && content.right().get().test(stack.asFluidOrEmpty());
        }
    }

    public boolean test(ItemStack stack) {
        return content.left().map(sized -> sized.test(stack)).orElse(false);
    }

    public boolean test(FluidStack stack) {
        return content.right().map(sized -> sized.test(stack)).orElse(false);
    }

    public static final Codec<SizedThingIngredient> CODEC =
            Codec.either(SizedIngredient.CODEC, SizedFluidIngredient.FLAT_CODEC)
                    .xmap(SizedThingIngredient::new, SizedThingIngredient::content);

    public static final StreamCodec<RegistryFriendlyByteBuf, SizedThingIngredient> STREAM_CODEC =
            ByteBufCodecs.either(SizedIngredient.STREAM_CODEC, SizedFluidIngredient.STREAM_CODEC)
                    .map(SizedThingIngredient::new, SizedThingIngredient::content);
}
