package io.github.rontyamc.lucentics.recipes.dyeing;

import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsFluidRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Optional;

public class DyeingRecipeConverter {
    public static final int BASE_MB_PER_DYE = 400;
    public static final int BASE_PROCESSING_DURATION = 15;

    private DyeingRecipeConverter() {}

    public static Optional<DyeingRecipeEntry> fromCrafting(Recipe<CraftingInput> recipe, HolderLookup.Provider registries) {
        var ingredients = recipe.getIngredients();
        if (ingredients.isEmpty()) return Optional.empty();

        Colors dyeColor = null;
        NonNullList<Ingredient> notDye = NonNullList.create();

        for (var ingredient : ingredients) {
            Optional<Colors> extracted = extractColor(ingredient);
            if (extracted.isEmpty()) {
                notDye.add(ingredient);
                continue;
            }

            if (dyeColor != null && !dyeColor.equals(extracted.get())) return Optional.empty();
            dyeColor = extracted.get();
        }

        if (dyeColor == null) return Optional.empty();

        Ingredient dyeable = null;
        int notDyeCount = 0;

        for (var ingredient : notDye) {
            if (dyeable == null || dyeable.equals(ingredient))  {
                dyeable = ingredient;
                notDyeCount++;
            }
            else return Optional.empty();
        }

        if (dyeable == null || notDyeCount <= 0) return Optional.empty();

        ItemStack result = recipe.getResultItem(registries);
        if (result.isEmpty()) return Optional.empty();

        int liquidAmount = (BASE_MB_PER_DYE + notDyeCount - 1) / notDyeCount;
        int outputCount = Math.max(1, result.getCount() / notDyeCount);

        return Optional.of(new DyeingRecipeEntry(dyeable, SizedFluidIngredient.of(LucenticsFluidRegister.DYE_LIQUIDS.get(dyeColor).get().getSource(), liquidAmount),
                result.copyWithCount(outputCount), BASE_PROCESSING_DURATION, Optional.empty()));
    }

    private static Optional<Colors> extractColor(Ingredient ingredient) {
        Colors extracted = null;
        for (ItemStack stack : ingredient.getItems()) {
            if (!(stack.getItem() instanceof DyeItem dyeItem)) return Optional.empty();

            Colors color = Colors.byDyeColor(dyeItem.getDyeColor());
            if (color == null) return Optional.empty();
            if (extracted != null && color != extracted) return Optional.empty();
            extracted = color;
        }
        return Optional.ofNullable(extracted);
    }

    public static DyeingRecipeEntry fromJson(DyeingRecipe recipe, ResourceLocation id) {
        DyeingRecipeArguments args = recipe.getArguments();
        return new DyeingRecipeEntry(args.input(), args.fluid(), args.output(), args.processingDuration(), Optional.of(id));
    }
}
