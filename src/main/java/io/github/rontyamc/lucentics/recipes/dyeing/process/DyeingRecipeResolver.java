package io.github.rontyamc.lucentics.recipes.dyeing.process;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.recipes.dyeing.DyeingRecipeConverter;
import io.github.rontyamc.lucentics.recipes.dyeing.DyeingRecipeEntry;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = Lucentics.MOD_ID)
public class DyeingRecipeResolver {
    private static Map<Item, Map<Fluid, DyeingRecipeEntry>> index = null;

    private DyeingRecipeResolver() {}

    public record Result(ItemStack output, int liquidAmount, int processingDuration, Optional<ResourceLocation> recipeId) {}

    public static Optional<Result> resolve(Level level, ItemStack dyeable, FluidStack fluid) {
        if (dyeable.isEmpty() || fluid.isEmpty()) return Optional.empty();

        if (index == null) {
            index = build(level.getRecipeManager(), level.registryAccess());
        }

        Map<Fluid, DyeingRecipeEntry> entryMap = index.get(dyeable.getItem());
        if (entryMap == null) return Optional.empty();

        DyeingRecipeEntry entry = entryMap.get(fluid.getFluid());
        if (entry == null) return Optional.empty();

        return Optional.of(new Result(entry.result().copy(), entry.fluid().amount(), entry.processingDuration(), entry.recipeId()));
    }

    private static Map<Item, Map<Fluid, DyeingRecipeEntry>> build(RecipeManager manager, HolderLookup.Provider registries) {
        Map<Item, Map<Fluid, DyeingRecipeEntry>> built = new HashMap<>();


        for (var holder : manager.getAllRecipesFor(RecipeType.CRAFTING)) {
            DyeingRecipeConverter.fromCrafting(holder.value(), registries).ifPresent(entry -> putEntry(built, entry));
        }

        for (var holder : manager.getAllRecipesFor(LucenticsRecipeTypesRegister.DYEING_TYPE.get())) {
            DyeingRecipeEntry entry = DyeingRecipeConverter.fromJson(holder.value(), holder.id());
            putEntry(built, entry);
        }

        return built;
    }

    private static void putEntry(Map<Item, Map<Fluid, DyeingRecipeEntry>> built, DyeingRecipeEntry entry) {
        for (ItemStack stack : entry.ingredient().getItems()) {
            for (FluidStack fluid : entry.fluid().getFluids()) {
                built.computeIfAbsent(stack.getItem(), i -> new HashMap<>()).put(fluid.getFluid(), entry);
            }
        }
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener((ResourceManagerReloadListener) resourceManager -> index = null);
    }
}
