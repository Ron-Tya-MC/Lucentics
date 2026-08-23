package io.github.rontyamc.lucentics;

import io.github.rontyamc.lucentics.blocks.emitter.EmitterRenderer;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRenderer;
import io.github.rontyamc.lucentics.blocks.pedestals.PedestalRenderer;
import io.github.rontyamc.lucentics.common.datagen.AddRawLang;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import io.github.rontyamc.lucentics.registers.*;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Lucentics.MOD_ID)
public class Lucentics {
    public static final String MOD_ID = "lucentics";
    public static final String MOD_NAME = "Lucentics";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final LucenticsRegistrate REGISTRATE = LucenticsRegistrate.create(MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public Lucentics(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        REGISTRATE.registerEventListeners(modEventBus);

        LucenticsTabRegister.register(modEventBus);
        LucenticsBlockRegister.register();
        LucenticsBlockEntityRegister.register();
        LucenticsItemRegister.register();

        LucenticsRecipeTypesRegister.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerCapabilities);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        
        AddRawLang.register();
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    public static LucenticsRegistrate registrate() {
        return REGISTRATE;
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                LucenticsBlockEntityRegister.INJECTOR.get(),
                (be, side) -> be.getInjectorBehavior().iHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                LucenticsBlockEntityRegister.EMITTER.get(),
                (be, side) -> be.getEmitterBehavior().iHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                LucenticsBlockEntityRegister.ENGRAVING_TABLE.get(),
                (be, side) -> be.getEngravingTableBehavior().iHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                LucenticsBlockEntityRegister.PEDESTAL_RITUAL.get(),
                (be, side) -> be.getPedestalRitualBehavior().iHandler
        );
    }

    @EventBusSubscriber(modid = Lucentics.MOD_ID, value = Dist.CLIENT)
    public static class LucenticsClient {
        public LucenticsClient(ModContainer container) {
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(LucenticsBlockRegister.PRISM_RITUAL.get(), RenderType.translucent());
            });
        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event){
            event.registerBlockEntityRenderer(LucenticsBlockEntityRegister.INJECTOR.get(), InjectorRenderer::new);
            event.registerBlockEntityRenderer(LucenticsBlockEntityRegister.EMITTER.get(), EmitterRenderer::new);
            event.registerBlockEntityRenderer(LucenticsBlockEntityRegister.PEDESTAL_RITUAL.get(), PedestalRenderer::new);   
        }
    }
}