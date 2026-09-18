package io.github.rontyamc.lucentics.recipes.dyeing;

import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class DyeingRecipeCollector {
    private DyeingRecipeCollector() {}

    public static List<DyeingRecipeEntry> collect(RecipeManager manager, HolderLookup.Provider registries) {
        List<DyeingRecipeEntry> result = new ArrayList<>();

        for (var holder : manager.getAllRecipesFor(RecipeType.CRAFTING)) {
            DyeingRecipeConverter.from(holder.value(), registries).ifPresent(result::add);
        }

        for (var holder : manager.getAllRecipesFor(LucenticsRecipeTypesRegister.DYEING_TYPE.get())) {
            result.add(DyeingRecipeConverter.fromJson(holder.value()));
        }

        return result;
    }
}
