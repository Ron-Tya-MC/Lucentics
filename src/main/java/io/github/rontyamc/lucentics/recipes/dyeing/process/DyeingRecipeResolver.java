package io.github.rontyamc.lucentics.recipes.dyeing.process;

import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.recipes.dyeing.DyeingRecipeConverter;
import io.github.rontyamc.lucentics.recipes.dyeing.DyeingRecipeEntry;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DyeingRecipeResolver {
    private static Map<Item, Map<Colors, DyeingRecipeEntry>> index = null;

    private DyeingRecipeResolver() {}

    public record Result(ItemStack output, int liquidAmount) {}

    public static Optional<Result> resolve(Level level, ItemStack dyeable, Colors color) {
        if (dyeable.isEmpty() || color.getDyeColor() == null) return Optional.empty();

        if (index == null) {
            index = build(level.getRecipeManager(), level.registryAccess());
        }

        Map<Colors, DyeingRecipeEntry> entryMap = index.get(dyeable.getItem());
        if (entryMap == null) return Optional.empty();

        DyeingRecipeEntry entry = entryMap.get(color);
        if (entry == null) return Optional.empty();

        return Optional.of(new Result(entry.result().copy(), entry.liquidAmount()));
    }

    private static Map<Item, Map<Colors, DyeingRecipeEntry>> build(RecipeManager manager, HolderLookup.Provider registries) {
        Map<Item, Map<Colors, DyeingRecipeEntry>> built = new HashMap<>();


        for (var holder : manager.getAllRecipesFor(RecipeType.CRAFTING)) {
            DyeingRecipeConverter.from(holder.value(), registries).ifPresent(entry -> putEntry(built, entry));
        }

        for (var holder : manager.getAllRecipesFor(LucenticsRecipeTypesRegister.DYEING_TYPE.get())) {
            DyeingRecipeEntry entry = DyeingRecipeConverter.fromJson(holder.value());
            putEntry(built, entry);
        }

        return built;
    }

    private static void putEntry(Map<Item, Map<Colors, DyeingRecipeEntry>> built, DyeingRecipeEntry entry) {
        for (ItemStack stack : entry.ingredient().getItems()) {
            built.computeIfAbsent(stack.getItem(), i -> new HashMap<>()).put(entry.color(), entry);
        }
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        index = null;
    }
}
