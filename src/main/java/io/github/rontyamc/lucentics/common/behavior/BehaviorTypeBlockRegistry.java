package io.github.rontyamc.lucentics.common.behavior;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BehaviorTypeBlockRegistry {
    private static final Map<ResourceLocation, List<Supplier<ItemStack>>> PROVIDERS = new HashMap<>();

    public static void register(Supplier<ItemStack> stackSupplier, BehaviorType<?>... type) {
        for (BehaviorType<?> behavior : type) {
            PROVIDERS.computeIfAbsent(behavior.getId(), id -> new ArrayList<>()).add(stackSupplier);
        }
    }

    public static List<ItemStack> getProviders(BehaviorType<?> type) {
        return PROVIDERS.getOrDefault(type.getId(), List.of())
                .stream()
                .map(Supplier::get)
                .toList();
    }
}
