package io.github.rontyamc.lucentics.common.datagen;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.datagen.providers.RecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Lucentics.MOD_ID)
public class LucenticsDatagen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new LangProviderJp(output));
        generator.addProvider(event.includeServer(), new RecipeProvider(output, event.getLookupProvider()));
    }
}
