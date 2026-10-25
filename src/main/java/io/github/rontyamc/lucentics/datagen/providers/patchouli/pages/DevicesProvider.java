package io.github.rontyamc.lucentics.datagen.providers.patchouli.pages;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.client.patchouli.CraftingPage;
import io.github.rontyamc.lucentics.datagen.providers.advancement.LucenticsAdvancementGenerator;
import io.github.rontyamc.lucentics.datagen.providers.patchouli.LucenticsPatchouliProvider;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import net.minecraft.resources.ResourceLocation;

public class DevicesProvider {
    private DevicesProvider() {}

    private static int index = 0;
    public static final ResourceLocation CATEGORY_ID = Lucentics.path("devices");

    public static void build(LucenticsPatchouliProvider provider) {
        provider.category(CATEGORY_ID)
                .icon(LucenticsBlockRegister.EMITTER)
                .sortnum(1);

        provider.entry(CATEGORY_ID, "engraving_table")
                .icon(LucenticsBlockRegister.ENGRAVING_TABLE)
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.DAWNSTONE)
                .text("intro")
                .text("intro2")
                .page(new CraftingPage(Lucentics.path("crafting/functional/engraving_table")).setText("engraving"));

        provider.entry(CATEGORY_ID, "milling_table")
                .icon(LucenticsBlockRegister.MILLING_TABLE)
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.LIGHT_COPPER)
                .text("intro")
                .page(new CraftingPage(Lucentics.path("crafting/functional/milling_table")).setText("misc"));

        provider.entry(CATEGORY_ID, "mixing_table")
                .icon(LucenticsBlockRegister.MIXING_TABLE)
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.LIGHT_COPPER)
                .text("intro")
                .page(new CraftingPage(Lucentics.path("crafting/functional/mixing_table")).setText("misc"));

        provider.entry(CATEGORY_ID, "assembling_table")
                .icon(LucenticsBlockRegister.ASSEMBLING_TABLE)
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.LIGHT_COPPER)
                .text("intro")
                .page(new CraftingPage(Lucentics.path("crafting/functional/assembling_table")).setText("misc"));

        provider.entry(CATEGORY_ID, "pedestals")
                .icon(LucenticsBlockRegister.PEDESTAL_RITUAL)
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.DAWNSTONE)
                .page(new CraftingPage(Lucentics.path("crafting/functional/pedestal_ritual")).setText("ritual").setTitle("ritual"));

        provider.entry(CATEGORY_ID, "tank")
                .icon(LucenticsBlockRegister.TANK_LIGHT_COPPER)
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.LIGHT_COPPER)
                .text("intro")
                .page(new CraftingPage(Lucentics.path("crafting/functional/tank_light_copper")))
                .text("thick")
                .page(new CraftingPage(Lucentics.path("crafting/functional/tank_light_copper_thick")).addRecipe(Lucentics.path("crafting/functional/tank_light_copper_from_thick")));

        provider.entry(CATEGORY_ID, "toggled_emitter")
                .icon(LucenticsBlockRegister.TOGGLED_EMITTER)
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.EMITTER)
                .text("intro")
                .page(new CraftingPage(Lucentics.path("crafting/functional/toggled_emitter_from_emitter")));

        provider.entry(CATEGORY_ID, "advanced_emitter")
                .icon(LucenticsBlockRegister.ADVANCED_EMITTER)
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.GLIMMER_IRON)
                .text("intro")
                .page(new CraftingPage(Lucentics.path("crafting/functional/advanced_emitter")).addRecipe(Lucentics.path("crafting/functional/advanced_toggled_emitter")));
    }

    private static int index() {
        return ++index;
    }
}
