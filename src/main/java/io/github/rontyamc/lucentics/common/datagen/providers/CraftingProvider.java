package io.github.rontyamc.lucentics.common.datagen.providers;

import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

public class CraftingProvider {
    public CraftingProvider() {}

    protected static void buildRecipes(RecipeProvider provider, RecipeOutput recipeOutput) {
        provider.enterFolder("building");

        provider.generic(LucenticsBlockRegister.DUSK_BRICKS).unlockedBy(LucenticsItemRegister.DUSK_BRICK)
                .shaped(b -> b.define('D', LucenticsItemRegister.DUSK_BRICK.get())
                        .pattern("DD")
                        .pattern("DD"));


        provider.enterFolder("machines");

        provider.generic(LucenticsBlockRegister.INJECTOR).unlockedBy(() -> Items.BRICK)
                .shaped(b -> b.define('B', Items.BRICK)
                        .define('S', Items.STICK)
                        .define('G', Items.GLASS_PANE)
                        .define('C', Items.COBBLESTONE)
                        .define('H', Items.CHISELED_STONE_BRICKS)
                        .pattern("BGB")
                        .pattern("S S")
                        .pattern("CHC"));

        provider.generic(LucenticsBlockRegister.EMITTER).unlockedBy(LucenticsBlockRegister.DUSK_BRICKS)
                .shaped(b -> b.define('D', LucenticsBlockRegister.DUSK_BRICKS)
                        .define('G', Items.GOLD_INGOT)
                        .define('R', Items.REDSTONE)
                        .pattern("DDD")
                        .pattern("DGD")
                        .pattern("DRD"));

        provider.generic(LucenticsBlockRegister.ENGRAVING_TABLE).unlockedBy(() -> Items.DIAMOND)
                .shaped(b -> b.define('B', LucenticsBlockRegister.DUSK_BRICKS.get())
                        .define('S', LucenticsBlockRegister.DAWNSTONE.get())
                        .define('D', Items.DIAMOND)
                        .pattern("SSS")
                        .pattern("SDS")
                        .pattern("BBB"));

        provider.generic(LucenticsBlockRegister.PEDESTAL_RITUAL).unlockedBy(LucenticsBlockRegister.DAWNSTONE)
                .shaped(b -> b.define('D', LucenticsBlockRegister.DAWNSTONE.get())
                        .define('G', Items.GOLD_INGOT)
                        .pattern("DGD")
                        .pattern(" D ")
                        .pattern("DDD"));


        provider.enterFolder("ingredients");

        provider.generic(LucenticsItemRegister.LENS_FRAME).unlockedBy(LucenticsItemRegister.LIGHT_COPPER)
                .shaped(b -> b.define('L', LucenticsItemRegister.LIGHT_COPPER.get())
                        .pattern("LLL")
                        .pattern("L L")
                        .pattern("LLL"));
    }
}
