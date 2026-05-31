package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rontyamc.lucentics.common.recipe.LucenticsRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record InjectorRecipe(Ingredient inputItem, ItemStack output) implements Recipe<InjectorRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(InjectorRecipeInput injectorRecipeInput, Level level) {
        if(level.isClientSide()) {
            return false;
        }

        return inputItem.test(injectorRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(InjectorRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LucenticsRecipes.INJECTOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return LucenticsRecipes.INJECTOR_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<InjectorRecipe> {
        public static final MapCodec<InjectorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(InjectorRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(InjectorRecipe::output)
        ).apply(inst, InjectorRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InjectorRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, InjectorRecipe::inputItem,
                ItemStack.STREAM_CODEC, InjectorRecipe::output,
                InjectorRecipe::new
        );

        @Override
        public MapCodec<InjectorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InjectorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
