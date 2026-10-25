package io.github.rontyamc.lucentics.integration.jade;

import io.github.rontyamc.lucentics.blocks.assembling_table.AssemblingTableBlock;
import io.github.rontyamc.lucentics.blocks.assembling_table.AssemblingTableBlockEntity;
import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlock;
import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlockEntity;
import io.github.rontyamc.lucentics.blocks.engraving_table.EngravingTableBlock;
import io.github.rontyamc.lucentics.blocks.engraving_table.EngravingTableBlockEntity;
import io.github.rontyamc.lucentics.blocks.injector.InjectorBlock;
import io.github.rontyamc.lucentics.blocks.injector.InjectorBlockEntity;
import io.github.rontyamc.lucentics.blocks.milling_table.MillingTableBlock;
import io.github.rontyamc.lucentics.blocks.milling_table.MillingTableBlockEntity;
import io.github.rontyamc.lucentics.blocks.mixing_table.MixingTableBlock;
import io.github.rontyamc.lucentics.blocks.mixing_table.MixingTableBlockEntity;
import io.github.rontyamc.lucentics.integration.jade.component_providers.EmitterComponentProvider;
import io.github.rontyamc.lucentics.integration.jade.component_providers.InjectorComponentProvider;
import io.github.rontyamc.lucentics.integration.jade.component_providers.TrailCraftingComponentProvider;
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
        registration.registerBlockDataProvider(TrailCraftingComponentProvider.INSTANCE, EngravingTableBlockEntity.class);
        registration.registerBlockDataProvider(TrailCraftingComponentProvider.INSTANCE, MillingTableBlockEntity.class);
        registration.registerBlockDataProvider(TrailCraftingComponentProvider.INSTANCE, MixingTableBlockEntity.class);
        registration.registerBlockDataProvider(TrailCraftingComponentProvider.INSTANCE, AssemblingTableBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(InjectorComponentProvider.INSTANCE, InjectorBlock.class);
        registration.registerBlockComponent(EmitterComponentProvider.INSTANCE, EmitterBlock.class);
        registration.registerBlockComponent(TrailCraftingComponentProvider.INSTANCE, EngravingTableBlock.class);
        registration.registerBlockComponent(TrailCraftingComponentProvider.INSTANCE, MillingTableBlock.class);
        registration.registerBlockComponent(TrailCraftingComponentProvider.INSTANCE, MixingTableBlock.class);
        registration.registerBlockComponent(TrailCraftingComponentProvider.INSTANCE, AssemblingTableBlock.class);
    }
}
