package io.github.rontyamc.lucentics.registers;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.advancement.LucenticsEventTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LucenticsTriggerRegister {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, Lucentics.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, LucenticsEventTrigger> EVENT =
            TRIGGERS.register("event", LucenticsEventTrigger::new);

    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
    }
}
