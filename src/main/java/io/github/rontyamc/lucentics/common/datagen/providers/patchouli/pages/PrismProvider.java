package io.github.rontyamc.lucentics.common.datagen.providers.patchouli.pages;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.client.patchouli.CraftingPage;
import io.github.rontyamc.lucentics.common.datagen.providers.patchouli.LucenticsPatchouliProvider;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import net.minecraft.resources.ResourceLocation;

public class PrismProvider {
    private PrismProvider() {}

    private static int index = 0;
    public static final ResourceLocation CATEGORY_ID = Lucentics.defaultLocation("prism");

    public static void build(LucenticsPatchouliProvider provider) {
        provider.category(CATEGORY_ID)
                .icon(LucenticsBlockRegister.PRISM_BLANK)
                .sortnum(2);

        provider.entry(CATEGORY_ID, "what_are_prisms")
                .icon(LucenticsBlockRegister.PRISM_BLANK)
                .sortnum(index())
                .text("intro")
                .text("intro2")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_blank")))
                .text("transporting", true)
                .text("transporting2", true);

        provider.entry(CATEGORY_ID, "prism_ritual")
                .icon(LucenticsBlockRegister.PRISM_RITUAL)
                .sortnum(index())
                .text("intro")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_ritual_from_blank")));

        provider.entry(CATEGORY_ID, "prism_importing")
                .icon(LucenticsBlockRegister.PRISM_IMPORTING)
                .sortnum(index())
                .text("intro")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_importing_from_blank")));

        provider.entry(CATEGORY_ID, "prism_exporting")
                .icon(LucenticsBlockRegister.PRISM_EXPORTING)
                .sortnum(index())
                .text("intro")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_exporting_from_blank")));

        provider.entry(CATEGORY_ID, "prism_io")
                .icon(LucenticsBlockRegister.PRISM_IO)
                .sortnum(index())
                .text("intro")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_io_from_importing"))
                        .addRecipe(Lucentics.defaultLocation("crafting/prisms/prism_io_from_exporting")));

        provider.entry(CATEGORY_ID, "prism_collecting")
                .icon(LucenticsBlockRegister.PRISM_COLLECTING)
                .sortnum(index())
                .text("intro")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_collecting_from_io")));

        provider.entry(CATEGORY_ID, "prism_break")
                .icon(LucenticsBlockRegister.PRISM_BREAK)
                .sortnum(index())
                .text("intro")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_break")));

        provider.entry(CATEGORY_ID, "prism_attack")
                .icon(LucenticsBlockRegister.PRISM_ATTACK)
                .sortnum(index())
                .text("intro")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_attack")));

        provider.entry(CATEGORY_ID, "prism_fluid_drain")
                .icon(LucenticsBlockRegister.PRISM_FLUID_DRAIN)
                .sortnum(index())
                .text("intro")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_fluid_drain")));

        provider.entry(CATEGORY_ID, "prism_fluid_place")
                .icon(LucenticsBlockRegister.PRISM_FLUID_PLACE)
                .sortnum(index())
                .text("intro")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_fluid_place")));

        provider.entry(CATEGORY_ID, "prism_place")
                .icon(LucenticsBlockRegister.PRISM_PLACE)
                .sortnum(index())
                .text("intro")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_place")));

        provider.entry(CATEGORY_ID, "prism_dyeing")
                .icon(LucenticsBlockRegister.PRISM_DYEING)
                .sortnum(index())
                .text("intro")
                .text("intro2", true)
                .text("intro3")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/prisms/prism_dyeing")));
    }

    private static int index() {
        return ++index;
    }
}
