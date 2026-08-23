package io.github.rontyamc.lucentics.integration.jade;

import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlock;
import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlockEntity;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableBlock;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableBlockEntity;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBlock;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBlockEntity;
import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBlock;
import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBlockEntity;
import io.github.rontyamc.lucentics.integration.jade.component_providers.EmitterComponentProvider;
import io.github.rontyamc.lucentics.integration.jade.component_providers.EngravingTableComponentProvider;
import io.github.rontyamc.lucentics.integration.jade.component_providers.InjectorComponentProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class LucenticsJadeIntegration implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(InjectorComponentProvider.INSTANCE, InjectorBlockEntity.class);
        registration.registerBlockDataProvider(EmitterComponentProvider.INSTANCE, EmitterBlockEntity.class);
        registration.registerBlockDataProvider(EngravingTableComponentProvider.INSTANCE, EngravingTableBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(InjectorComponentProvider.INSTANCE, InjectorBlock.class);
        registration.registerBlockComponent(EmitterComponentProvider.INSTANCE, EmitterBlock.class);
        registration.registerBlockComponent(EngravingTableComponentProvider.INSTANCE, EngravingTableBlock.class);
    }
}
