package io.github.rontyamc.lucentics.common.datagen.providers.recipe;

import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class CraftingProvider {
    public CraftingProvider() {}

    protected static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.enterFolder("buildings");

        provider.generic(LucenticsBlockRegister.DUSK_BRICKS).unlockedBy(LucenticsItemRegister.DUSK_BRICK)
                .shaped(b -> b.define('D', LucenticsItemRegister.DUSK_BRICK.get())
                        .pattern("DD")
                        .pattern("DD"));
        provider.generic(LucenticsBlockRegister.RED_DUSK_BRICKS).unlockedBy(LucenticsItemRegister.RED_DUSK_BRICK)
                .shaped(b -> b.define('D', LucenticsItemRegister.RED_DUSK_BRICK.get())
                        .pattern("DD")
                        .pattern("DD"));
        provider.generic(LucenticsBlockRegister.BLUE_DUSK_BRICKS).unlockedBy(LucenticsItemRegister.BLUE_DUSK_BRICK)
                .shaped(b -> b.define('D', LucenticsItemRegister.BLUE_DUSK_BRICK.get())
                        .pattern("DD")
                        .pattern("DD"));
        provider.generic(LucenticsBlockRegister.GREEN_DUSK_BRICKS).unlockedBy(LucenticsItemRegister.GREEN_DUSK_BRICK)
                .shaped(b -> b.define('D', LucenticsItemRegister.GREEN_DUSK_BRICK.get())
                        .pattern("DD")
                        .pattern("DD"));

        provider.generic(LucenticsBlockRegister.LIGHT_COPPER_BLOCK).unlockedBy(LucenticsItemRegister.LIGHT_COPPER)
                .shaped(b -> b.define('L', LucenticsItemRegister.LIGHT_COPPER.get())
                        .pattern("LLL")
                        .pattern("LLL")
                        .pattern("LLL"));
        provider.generic(LucenticsBlockRegister.YELLOW_LIGHT_COPPER_BLOCK).unlockedBy(LucenticsItemRegister.YELLOW_LIGHT_COPPER)
                .shaped(b -> b.define('L', LucenticsItemRegister.YELLOW_LIGHT_COPPER.get())
                        .pattern("LLL")
                        .pattern("LLL")
                        .pattern("LLL"));
        provider.generic(LucenticsBlockRegister.MAGENTA_LIGHT_COPPER_BLOCK).unlockedBy(LucenticsItemRegister.MAGENTA_LIGHT_COPPER)
                .shaped(b -> b.define('L', LucenticsItemRegister.MAGENTA_LIGHT_COPPER.get())
                        .pattern("LLL")
                        .pattern("LLL")
                        .pattern("LLL"));
        provider.generic(LucenticsBlockRegister.CYAN_LIGHT_COPPER_BLOCK).unlockedBy(LucenticsItemRegister.CYAN_LIGHT_COPPER)
                .shaped(b -> b.define('L', LucenticsItemRegister.CYAN_LIGHT_COPPER.get())
                        .pattern("LLL")
                        .pattern("LLL")
                        .pattern("LLL"));

        provider.generic(() -> Blocks.GLASS).unlockedBy(LucenticsItemRegister.GLASS_SHERD)
                .shaped(b -> b.define('S', LucenticsItemRegister.GLASS_SHERD.get())
                        .pattern("SS")
                        .pattern("SS"));

        provider.enterFolder("functional");

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
        provider.generic(LucenticsBlockRegister.TOGGLED_EMITTER).unlockedBy(LucenticsBlockRegister.EMITTER)
                .shapeless(b -> b.requires(LucenticsBlockRegister.EMITTER)
                        .requires(Blocks.REDSTONE_TORCH));

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

        provider.generic(LucenticsBlockRegister.MILLING_TABLE).unlockedBy(LucenticsBlockRegister.RED_DUSK_BRICKS)
                .shaped(b -> b.define('B', LucenticsBlockRegister.RED_DUSK_BRICKS.get())
                        .define('L', LucenticsItemRegister.LIGHT_COPPER)
                        .define('D', LucenticsBlockRegister.DAWNSTONE)
                        .pattern(" B ")
                        .pattern("LDL")
                        .pattern("BBB"));
        provider.generic(LucenticsBlockRegister.MIXING_TABLE).unlockedBy(LucenticsBlockRegister.BLUE_DUSK_BRICKS)
                .shaped(b -> b.define('B', LucenticsBlockRegister.BLUE_DUSK_BRICKS.get())
                        .define('T', LucenticsBlockRegister.TANK_LIGHT_COPPER)
                        .define('D', LucenticsBlockRegister.DAWNSTONE)
                        .pattern("DDD")
                        .pattern(" T ")
                        .pattern("BBB"));
        provider.generic(LucenticsBlockRegister.ASSEMBLING_TABLE).unlockedBy(LucenticsBlockRegister.GREEN_DUSK_BRICKS)
                .shaped(b -> b.define('B', LucenticsBlockRegister.GREEN_DUSK_BRICKS.get())
                        .define('L', LucenticsItemRegister.LIGHT_COPPER)
                        .define('D', LucenticsBlockRegister.DAWNSTONE)
                        .pattern("DDD")
                        .pattern("LBL")
                        .pattern("BBB"));

        provider.generic(LucenticsBlockRegister.TANK_LIGHT_COPPER).unlockedBy(LucenticsItemRegister.LIGHT_COPPER)
                .shaped(b -> b.define('L', LucenticsItemRegister.LIGHT_COPPER.get())
                        .define('G', Items.GLASS)
                        .pattern(" L ")
                        .pattern("LGL")
                        .pattern(" L "));
        provider.generic(LucenticsBlockRegister.TANK_LIGHT_COPPER_BOLD).unlockedBy(LucenticsBlockRegister.TANK_LIGHT_COPPER)
                .shapeless(b -> b.requires(LucenticsBlockRegister.TANK_LIGHT_COPPER.get()));
        provider.generic(LucenticsBlockRegister.TANK_LIGHT_COPPER).unlockedBy(LucenticsBlockRegister.TANK_LIGHT_COPPER_BOLD)
                .suffix("_from_bold")
                .shapeless(b -> b.requires(LucenticsBlockRegister.TANK_LIGHT_COPPER_BOLD.get()));


        provider.enterFolder("ingredients");

        provider.generic(LucenticsItemRegister.LENS_FRAME).unlockedBy(LucenticsItemRegister.LIGHT_COPPER)
                .shaped(b -> b.define('L', LucenticsItemRegister.LIGHT_COPPER.get())
                        .pattern("LLL")
                        .pattern("L L")
                        .pattern("LLL"));
        

        provider.enterFolder("prisms");

        provider.generic(LucenticsBlockRegister.PRISM_BLANK).unlockedBy(LucenticsItemRegister.DAWNSTONE_DUST)
                .shaped(b -> b.define('D', LucenticsItemRegister.DAWNSTONE_DUST.get())
                        .define('G', Items.GLASS)
                        .pattern(" D ")
                        .pattern("DGD")
                        .pattern(" D "));

        provider.generic(LucenticsBlockRegister.PRISM_RITUAL).unlockedBy(LucenticsItemRegister.DAWNSTONE_DUST)
                .shaped(b -> b.define('D', LucenticsItemRegister.DAWNSTONE_DUST.get())
                        .define('G', Items.GLASS)
                        .define('L', Items.LAPIS_LAZULI)
                        .pattern("LDL")
                        .pattern("DGD")
                        .pattern("LDL"));
        provider.generic(LucenticsBlockRegister.PRISM_RITUAL).unlockedBy(LucenticsBlockRegister.PRISM_BLANK)
                .suffix("_from_blank")
                .shaped(b -> b.define('P', LucenticsBlockRegister.PRISM_BLANK.get())
                        .define('L', Items.LAPIS_LAZULI)
                        .pattern(" L ")
                        .pattern("LPL")
                        .pattern(" L "));

        provider.generic(LucenticsBlockRegister.PRISM_IMPORTING).unlockedBy(LucenticsItemRegister.DAWNSTONE_DUST)
                .shaped(b -> b.define('D', LucenticsItemRegister.DAWNSTONE_DUST.get())
                        .define('G', Items.GLASS)
                        .define('H', Items.HOPPER)
                        .pattern("DHD")
                        .pattern(" G ")
                        .pattern("D D"));
        provider.generic(LucenticsBlockRegister.PRISM_IMPORTING).unlockedBy(LucenticsBlockRegister.PRISM_BLANK)
                .suffix("_from_blank")
                .shaped(b -> b.define('P', LucenticsBlockRegister.PRISM_BLANK.get())
                        .define('H', Items.HOPPER)
                        .pattern("H")
                        .pattern("P"));

        provider.generic(LucenticsBlockRegister.PRISM_EXPORTING).unlockedBy(LucenticsItemRegister.DAWNSTONE_DUST)
                .shaped(b -> b.define('D', LucenticsItemRegister.DAWNSTONE_DUST.get())
                        .define('G', Items.GLASS)
                        .define('R', Items.DROPPER)
                        .pattern("DRD")
                        .pattern(" G ")
                        .pattern("D D"));
        provider.generic(LucenticsBlockRegister.PRISM_EXPORTING).unlockedBy(LucenticsBlockRegister.PRISM_BLANK)
                .suffix("_from_blank")
                .shaped(b -> b.define('P', LucenticsBlockRegister.PRISM_BLANK.get())
                        .define('D', Items.DROPPER)
                        .pattern("D")
                        .pattern("P"));

        provider.generic(LucenticsBlockRegister.PRISM_IO).unlockedBy(LucenticsBlockRegister.PRISM_IMPORTING)
                .suffix("_from_importing")
                .shaped(b -> b.define('P', LucenticsBlockRegister.PRISM_IMPORTING.get())
                        .define('D', Items.DROPPER)
                        .pattern("D")
                        .pattern("P"));
        provider.generic(LucenticsBlockRegister.PRISM_IO).unlockedBy(LucenticsBlockRegister.PRISM_EXPORTING)
                .suffix("_from_exporting")
                .shaped(b -> b.define('P', LucenticsBlockRegister.PRISM_EXPORTING.get())
                        .define('H', Items.HOPPER)
                        .pattern("H")
                        .pattern("P"));

        provider.generic(LucenticsBlockRegister.PRISM_COLLECTING).unlockedBy(LucenticsBlockRegister.PRISM_IMPORTING)
                .suffix("_from_importing")
                .shaped(b -> b.define('P', LucenticsBlockRegister.PRISM_IMPORTING.get())
                        .define('D', Items.DROPPER)
                        .define('B', Blocks.IRON_BARS)
                        .pattern(" D ")
                        .pattern("BPB"));
        provider.generic(LucenticsBlockRegister.PRISM_COLLECTING).unlockedBy(LucenticsBlockRegister.PRISM_EXPORTING)
                .suffix("_from_exporting")
                .shaped(b -> b.define('P', LucenticsBlockRegister.PRISM_EXPORTING.get())
                        .define('H', Items.HOPPER)
                        .define('B', Blocks.IRON_BARS)
                        .pattern(" H ")
                        .pattern("BPB"));
        provider.generic(LucenticsBlockRegister.PRISM_COLLECTING).unlockedBy(LucenticsBlockRegister.PRISM_IO)
                .suffix("_from_io")
                .shaped(b -> b.define('P', LucenticsBlockRegister.PRISM_IO.get())
                        .define('B', Blocks.IRON_BARS)
                        .pattern("BPB"));

        provider.generic(LucenticsBlockRegister.PRISM_DYEING).unlockedBy(LucenticsBlockRegister.COLORED_PRISM_BLANK.get(Colors.BLUE))
                .shaped(b -> b.define('P', LucenticsBlockRegister.COLORED_PRISM_BLANK.get(Colors.BLUE))
                        .define('D', LucenticsTagRegister.LucenticsITags.DYES.tag)
                        .pattern(" D ")
                        .pattern("DPD")
                        .pattern(" D "));


        provider.enterFolder("tools");

        provider.generic(LucenticsItemRegister.COPPER_HAMMER).unlockedBy(LucenticsItemRegister.DUSK_BRICK)
                .shaped(b -> b.define('D', LucenticsItemRegister.DUSK_BRICK.get())
                        .define('M', Items.COPPER_INGOT)
                        .define('B', Items.COPPER_BLOCK)
                        .define('S', Items.STICK)
                        .pattern("BDM")
                        .pattern(" S ")
                        .pattern(" S "));
        provider.generic(LucenticsItemRegister.IRON_HAMMER).unlockedBy(LucenticsItemRegister.DUSK_BRICK)
                .shaped(b -> b.define('D', LucenticsItemRegister.DUSK_BRICK.get())
                        .define('M', Items.IRON_INGOT)
                        .define('B', Items.IRON_BLOCK)
                        .define('S', Items.STICK)
                        .pattern("BDM")
                        .pattern(" S ")
                        .pattern(" S "));
        provider.generic(LucenticsItemRegister.DIAMOND_HAMMER).unlockedBy(LucenticsItemRegister.DUSK_BRICK)
                .shaped(b -> b.define('D', LucenticsItemRegister.DUSK_BRICK.get())
                        .define('M', Items.DIAMOND)
                        .define('B', Items.DIAMOND_BLOCK)
                        .define('S', Items.STICK)
                        .pattern("BDM")
                        .pattern(" S ")
                        .pattern(" S "));
        provider.generic(LucenticsItemRegister.GOLDEN_HAMMER).unlockedBy(LucenticsItemRegister.DUSK_BRICK)
                .shaped(b -> b.define('D', LucenticsItemRegister.DUSK_BRICK.get())
                        .define('M', Items.GOLD_INGOT)
                        .define('B', Items.GOLD_BLOCK)
                        .define('S', Items.STICK)
                        .pattern("BDM")
                        .pattern(" S ")
                        .pattern(" S "));
    }
}
