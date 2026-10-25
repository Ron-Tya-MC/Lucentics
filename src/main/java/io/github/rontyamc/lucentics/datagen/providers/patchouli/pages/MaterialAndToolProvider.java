package io.github.rontyamc.lucentics.datagen.providers.patchouli.pages;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.client.patchouli.CraftingPage;
import io.github.rontyamc.lucentics.client.patchouli.SpotlightPage;
import io.github.rontyamc.lucentics.datagen.providers.advancement.LucenticsAdvancementGenerator;
import io.github.rontyamc.lucentics.datagen.providers.patchouli.LucenticsPatchouliProvider;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsFluidRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class MaterialAndToolProvider {
    private MaterialAndToolProvider() {}

    private static int index = 0;
    public static final ResourceLocation CATEGORY_ID = Lucentics.path("material_and_tool");

    public static void build(LucenticsPatchouliProvider provider) {
        provider.category(CATEGORY_ID)
                .icon(LucenticsItemRegister.COPPER_HAMMER)
                .sortnum(3);

        provider.entry(CATEGORY_ID, "stones")
                .icon(LucenticsBlockRegister.DAWNSTONE)
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.INJECTOR)
                .page(new SpotlightPage(LucenticsItemRegister.DUSK_BRICK.asItem()).setText("dusk_brick"))
                .page(new SpotlightPage(LucenticsBlockRegister.DAWNSTONE.asItem()).setText("dawnstone"));

        provider.entry(CATEGORY_ID, "hammer")
                .icon(LucenticsItemRegister.COPPER_HAMMER)
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.DUSK_BRICK)
                .text("intro")
                .text("intro2")
                .page(new CraftingPage(Lucentics.path("crafting/tools/copper_hammer")).addRecipe(Lucentics.path("crafting/tools/iron_hammer")))
                .page(new CraftingPage(Lucentics.path("crafting/tools/diamond_hammer")).addRecipe(Lucentics.path("crafting/tools/golden_hammer")));

        provider.entry(CATEGORY_ID, "crushing_vanilla_block")
                .icon(() -> Items.BRICK)
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.DUSK_BRICK)
                .text("intro")
                .text("intro2");

        List<ItemStack> dyeLiquids = new ArrayList<>();
        for (Colors color : Colors.values()) {
            if (color == Colors.SUNLIGHT) continue;
            LucenticsFluidRegister.DYE_LIQUIDS.get(color).getBucket().ifPresent(item -> dyeLiquids.add(new ItemStack(item)));
        }
        if (dyeLiquids.isEmpty()) dyeLiquids.add(new ItemStack(LucenticsItemRegister.NOTHING.asItem()));

        provider.entry(CATEGORY_ID, "dye_liquid")
                .icon(() -> LucenticsFluidRegister.DYE_LIQUIDS.get(Colors.RED).getBucket().orElse(Items.RED_DYE))
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.MIXING)
                .page(new SpotlightPage(dyeLiquids).setText("intro").setTitle("intro"));

        provider.entry(CATEGORY_ID, "patina_liquid")
                .icon(() -> LucenticsFluidRegister.PATINA_LIQUID.getBucket().orElse(LucenticsItemRegister.COPPER_DUST.asItem()))
                .sortnum(index())
                .advancement(LucenticsAdvancementGenerator.MIXING)
                .page(new SpotlightPage(LucenticsFluidRegister.PATINA_LIQUID.getBucket().orElse(LucenticsItemRegister.COPPER_DUST.asItem())).setText("intro").setTitle("intro"));
    }

    private static int index() {
        return ++index;
    }
}
