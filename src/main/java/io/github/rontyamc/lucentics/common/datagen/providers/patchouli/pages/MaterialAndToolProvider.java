package io.github.rontyamc.lucentics.common.datagen.providers.patchouli.pages;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.client.patchouli.CraftingPage;
import io.github.rontyamc.lucentics.client.patchouli.SpotlightPage;
import io.github.rontyamc.lucentics.common.datagen.providers.patchouli.LucenticsPatchouliProvider;
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
    public static final ResourceLocation CATEGORY_ID = Lucentics.defaultLocation("material_and_tool");

    public static void build(LucenticsPatchouliProvider provider) {
        provider.category(CATEGORY_ID)
                .icon(LucenticsItemRegister.COPPER_HAMMER)
                .sortnum(3);

        provider.entry(CATEGORY_ID, "stones")
                .icon(LucenticsBlockRegister.DAWNSTONE)
                .sortnum(index())
                .page(new SpotlightPage(LucenticsItemRegister.DUSK_BRICK.asItem()).setText("dusk_brick"))
                .page(new SpotlightPage(LucenticsBlockRegister.DAWNSTONE.asItem()).setText("dawnstone"));

        provider.entry(CATEGORY_ID, "hammer")
                .icon(LucenticsItemRegister.COPPER_HAMMER)
                .sortnum(index())
                .text("intro")
                .text("intro2")
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/tools/copper_hammer")).addRecipe(Lucentics.defaultLocation("crafting/tools/iron_hammer")))
                .page(new CraftingPage(Lucentics.defaultLocation("crafting/tools/diamond_hammer")).addRecipe(Lucentics.defaultLocation("crafting/tools/golden_hammer")));

        List<ItemStack> dyeLiquids = new ArrayList<>();
        for (Colors color : Colors.values()) {
            if (color == Colors.SUNLIGHT) continue;
            LucenticsFluidRegister.DYE_LIQUIDS.get(color).getBucket().ifPresent(item -> dyeLiquids.add(new ItemStack(item)));
        }
        if (dyeLiquids.isEmpty()) dyeLiquids.add(new ItemStack(LucenticsItemRegister.NOTHING.asItem()));

        provider.entry(CATEGORY_ID, "dye_liquid")
                .icon(() -> LucenticsFluidRegister.DYE_LIQUIDS.get(Colors.RED).getBucket().orElse(Items.RED_DYE))
                .sortnum(index())
                .page(new SpotlightPage(dyeLiquids).setText("intro"));

        provider.entry(CATEGORY_ID, "patina_liquid")
                .icon(() -> LucenticsFluidRegister.PATINA_LIQUID.getBucket().orElse(LucenticsItemRegister.COPPER_DUST.asItem()))
                .sortnum(index())
                .page(new SpotlightPage(LucenticsFluidRegister.PATINA_LIQUID.getBucket().orElse(LucenticsItemRegister.COPPER_DUST.asItem())).setText("intro"));
    }

    private static int index() {
        return ++index;
    }
}
