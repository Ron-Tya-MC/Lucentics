package io.github.rontyamc.lucentics.registers;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.advancement.AdvancementEventType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LucenticsAdvancementEventRegister {
    public static final DeferredRegister<AdvancementEventType> EVENTS =
            DeferredRegister.create(LucenticsRegistries.ADVANCEMENT_EVENT_KEY, Lucentics.MOD_ID);

    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> CRUSHING_COMPLETED = event("crushing_completed");
    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> INJECTING_COMPLETED = event("injecting_completed");
    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> ENGRAVING_COMPLETED = event("engraving_completed");
    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> MILLING_COMPLETED = event("milling_completed");
    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> MIXING_COMPLETED = event("mixing_completed");
    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> ASSEMBLING_COMPLETED = event("assembling_completed");
    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> DYEING_COMPLETED = event("dyeing_completed");

    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> BEAM_EMITTED = event("beam_emitted");
    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> BEAM_RECEIVED = event("beam_received");

    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> PRISM_TRANSPORTED = event("prism_transported");
    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> PRISM_BREAK_ACTIVATED = event("prism_break_activated");
    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> PRISM_ATTACK_ACTIVATED = event("prism_attack_activated");
    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> PRISM_FLUID_PLACE_ACTIVATED = event("prism_fluid_place_activated");
    public static final DeferredHolder<AdvancementEventType, AdvancementEventType> PRISM_PLACE_ACTIVATED = event("prism_place_activated");

    private static DeferredHolder<AdvancementEventType, AdvancementEventType> event(String name) {
        return EVENTS.register(name, AdvancementEventType::new);
    }

    public static void register(IEventBus bus) {
        EVENTS.register(bus);
    }
}
