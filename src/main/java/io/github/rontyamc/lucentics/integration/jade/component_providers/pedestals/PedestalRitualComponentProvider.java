package io.github.rontyamc.lucentics.integration.jade.component_providers.pedestals;

import io.github.rontyamc.lucentics.integration.jade.component_providers.PedestalComponentProvider;
import net.minecraft.resources.ResourceLocation;

public class PedestalRitualComponentProvider extends PedestalComponentProvider {
    public static final PedestalRitualComponentProvider INSTANCE = new PedestalRitualComponentProvider();
    private PedestalRitualComponentProvider() {}

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("lucentics", "pedestal_ritual");
    }
}
