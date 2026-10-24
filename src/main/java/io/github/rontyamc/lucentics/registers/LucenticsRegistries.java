package io.github.rontyamc.lucentics.registers;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.advancement.AdvancementEventType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class LucenticsRegistries {
    public static final ResourceKey<Registry<AdvancementEventType>> ADVANCEMENT_EVENT_KEY =
            ResourceKey.createRegistryKey(Lucentics.path("advancement_event"));

    public static final Registry<AdvancementEventType> ADVANCEMENT_EVENT =
            new RegistryBuilder<>(ADVANCEMENT_EVENT_KEY).sync(false).create();

    public static void register(IEventBus bus) {
        bus.addListener(NewRegistryEvent.class, event -> event.register(ADVANCEMENT_EVENT));
    }
}
