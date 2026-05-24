package io.github.rontyamc.lucentics;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import io.github.rontyamc.lucentics.registers.LucenticsTabRegister;
import io.github.rontyamc.lucentics.registers.LucenticsRegistrate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

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

        LucenticsBlockRegister.register();
        LucenticsItemRegister.register();
        LucenticsTabRegister.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(LucenticsItemRegister.DUSK_BRICK.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    public static LucenticsRegistrate registrate() {
        return REGISTRATE;
    }
}