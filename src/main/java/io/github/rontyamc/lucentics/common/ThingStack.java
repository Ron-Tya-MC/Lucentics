package io.github.rontyamc.lucentics.common;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import io.github.rontyamc.lucentics.common.util.FluidUtil;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public record ThingStack(Either<ItemStack, FluidStack> content) {
    public static final ThingStack EMPTY = new ThingStack(Either.left(ItemStack.EMPTY));

    public static ThingStack of(ItemStack stack) { return new ThingStack(Either.left(stack)); }
    public static ThingStack of(FluidStack stack) { return new ThingStack(Either.right(stack)); }

    public static List<ThingStack> fromItems(List<ItemStack> stacks) { return stacks.stream().map(ThingStack::of).toList(); }
    public static List<ThingStack> fromFluids(List<FluidStack> stacks) { return stacks.stream().map(ThingStack::of).toList(); }

    public boolean isItem() { return content.left().isPresent(); }
    public boolean isFluid() { return content.right().isPresent(); }

    public boolean isEmpty() {
        return content.map(ItemStack::isEmpty, FluidStack::isEmpty);
    }

    public int amount() {
        return content.map(ItemStack::getCount, FluidStack::getAmount);
    }

    public ThingStack copyWithAmount(int amount) {
        return content.map(
                item -> ThingStack.of(item.copyWithCount(amount)),
                fluid -> ThingStack.of(fluid.copyWithAmount(amount))
        );
    }

    public ThingStack shrunken(int amount) {
        return copyWithAmount(Math.max(0, amount() - amount));
    }

    public ThingStack grown(int amount) {
        return copyWithAmount(amount() + amount);
    }

    public boolean isSameThing(ThingStack other) {
        return content.map(
                item -> other.content.left().map(o -> ItemUtil.isSameItem(item, o, false)).orElse(false),
                fluid -> other.content.right().map(o -> FluidUtil.isSameFluid(fluid, o, false)).orElse(false)
        );
    }

    public ItemStack asItemOrEmpty()  { return content.left().orElse(ItemStack.EMPTY); }
    public FluidStack asFluidOrEmpty() { return content.right().orElse(FluidStack.EMPTY); }

    public Either<SizedIngredient, SizedFluidIngredient> asIngredient() {
        return content.map(
                item -> Either.left(SizedIngredient.of(item)),
                fluid -> Either.right(SizedFluidIngredient.of(fluid))
        );
    }

    public static final Codec<ThingStack> CODEC =
            Codec.either(ItemStack.CODEC, FluidStack.CODEC).xmap(ThingStack::new, ThingStack::content);

    public static final StreamCodec<RegistryFriendlyByteBuf, ThingStack> STREAM_CODEC =
            ByteBufCodecs.either(ItemStack.STREAM_CODEC, FluidStack.STREAM_CODEC).map(ThingStack::new, ThingStack::content);
}
