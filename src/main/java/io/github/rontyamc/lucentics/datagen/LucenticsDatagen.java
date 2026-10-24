package io.github.rontyamc.lucentics.datagen;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.datagen.providers.advancement.LucenticsAdvancementProvider;
import io.github.rontyamc.lucentics.datagen.providers.lang.LangProviderJp;
import io.github.rontyamc.lucentics.datagen.providers.patchouli.LucenticsPatchouliProvider;
import io.github.rontyamc.lucentics.datagen.providers.recipe.LucenticsRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Lucentics.MOD_ID)
public class LucenticsDatagen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new LangProviderJp(output));
        generator.addProvider(event.includeServer(), new LucenticsRecipeProvider(output, provider));
        generator.addProvider(event.includeServer(), new LucenticsAdvancementProvider(output, provider, fileHelper));
        generator.addProvider(event.includeClient() || event.includeServer(), new LucenticsPatchouliProvider(output));
    }
}
