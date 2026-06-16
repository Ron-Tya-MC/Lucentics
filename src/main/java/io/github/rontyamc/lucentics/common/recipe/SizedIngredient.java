package io.github.rontyamc.lucentics.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.Nullable;
import org.jline.terminal.Size;

import java.util.Objects;
import java.util.stream.Stream;

public final class SizedIngredient {
    private final Ingredient ingredient;
    private final int count;

    public static final SizedIngredient EMPTY = new SizedIngredient(Ingredient.EMPTY, 1);

    @Nullable
    private ItemStack[] cachedStacks;

    public SizedIngredient(Ingredient ingredient, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive. If you've intended to make ingredient inconsumable, use \"not_consume\" field instead.");
        }
        this.ingredient = ingredient;
        this.count = count;
    }

    public static final Codec<SizedIngredient> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    Ingredient.MAP_CODEC_NONEMPTY.forGetter(SizedIngredient::ingredient),
                    NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.POSITIVE_INT, "amount", 1).forGetter(SizedIngredient::count))
            .apply(inst, SizedIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SizedIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, SizedIngredient::ingredient,
            ByteBufCodecs.VAR_INT, SizedIngredient::count,
            SizedIngredient::new
    );

    public Ingredient ingredient() {
        return ingredient;
    }

    public int count() {
        return count;
    }

    public static SizedIngredient of(ItemLike item, int count) {
        return new SizedIngredient(Ingredient.of(item), count);
    }

    public static SizedIngredient of(ItemStack stack) {
        return new SizedIngredient(Ingredient.of(stack), stack.getCount());
    }

    public static SizedIngredient of(TagKey<Item> tag, int count) {
        return new SizedIngredient(Ingredient.of(tag), count);
    }

    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= count;
    }

    public ItemStack[] getItems() {
        if (cachedStacks == null) {
            cachedStacks = Stream.of(ingredient.getItems())
                    .map(s -> s.copyWithCount(count))
                    .toArray(ItemStack[]::new);
        }
        return cachedStacks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SizedIngredient other)) return false;
        return count == other.count && ingredient.equals(other.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient, count);
    }

    @Override
    public String toString() {
        return count + "x " + ingredient;
    }
}
