package io.github.rontyamc.lucentics.common.datagen.providers.patchouli.pages;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.client.patchouli.CraftingPage;
import io.github.rontyamc.lucentics.client.patchouli.RelationsPage;
import io.github.rontyamc.lucentics.client.patchouli.SpotlightPage;
import io.github.rontyamc.lucentics.common.datagen.providers.patchouli.LucenticsPatchouliProvider;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class GettingStartedProvider {
    private GettingStartedProvider() {}

    private static int index = 0;
    public static final ResourceLocation CATEGORY_ID = Lucentics.defaultLocation("getting_started");

    public static void build(LucenticsPatchouliProvider provider) {
        provider.category(CATEGORY_ID)
                .icon(LucenticsItemRegister.DUSK_BRICK)
                .sortnum(0);

        provider.entry(CATEGORY_ID, "welcome")
                .icon(LucenticsItemRegister.DUSK_BRICK)
                .sortnum(index())
                .text("intro")
                .text("intro2");

        provider.entry(CATEGORY_ID, "injector")
                .icon(LucenticsBlockRegister.INJECTOR)
                .sortnum(index())
                .text("intro")
                .page(new SpotlightPage(LucenticsBlockRegister.INJECTOR.asItem()).setText("injector"))
                .text("injector2")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/functional/injector")).setText("injector3"));

        provider.entry(CATEGORY_ID, "emitter")
                .icon(LucenticsBlockRegister.EMITTER)
                .sortnum(index())
                .text("intro")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/functional/emitter")).setText("emitter"))
                .text("emitter2");

        provider.entry(CATEGORY_ID, "prism")
                .icon(LucenticsBlockRegister.PRISM_BLANK)
                .sortnum(index())
                .text("intro")
                .page(new RelationsPage().addEntry(PrismProvider.CATEGORY_ID, "what_are_prisms"));

        provider.entry(CATEGORY_ID, "daylight")
                .icon(() -> Blocks.DAYLIGHT_DETECTOR)
                .sortnum(index())
                .text("intro");
    }

    private static int index() {
        return ++index;
    }
}
