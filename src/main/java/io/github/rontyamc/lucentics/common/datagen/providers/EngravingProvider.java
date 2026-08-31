package io.github.rontyamc.lucentics.common.datagen.providers;

import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBehavior;
import io.github.rontyamc.lucentics.common.datagen.builders.TrailBuilder;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

public class EngravingProvider {
    public EngravingProvider() {}

    protected static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.leaveFolder();

        provider.generic(LucenticsItemRegister.LIGHT_COPPER)
                .engraving(b -> b.input(Items.COPPER_INGOT)
                        .trail(TrailBuilder.create(Colors.SUNLIGHT)
                                .notConsumeInput(Items.IRON_NUGGET, PedestalRitualBehavior.TYPE)
                                .input(Items.GOLD_NUGGET, PedestalRitualBehavior.TYPE)
                                .input(Items.GOLD_NUGGET, PedestalRitualBehavior.TYPE)
                                .input(Items.GOLD_NUGGET, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.IRON_NUGGET, PedestalRitualBehavior.TYPE)
                        )
                        .duration(100)
                        .daylight(8));

        provider.generic(LucenticsItemRegister.LENSES.get(Colors.RED))
                .engraving(b -> b.input(LucenticsItemRegister.LENS_FRAME)
                        .trail(TrailBuilder.create(Colors.SUNLIGHT)
                                .input(Items.IRON_PICKAXE, PedestalRitualBehavior.TYPE)
                                .input(Items.IRON_AXE, PedestalRitualBehavior.TYPE)
                                .input(Items.IRON_SHOVEL, PedestalRitualBehavior.TYPE)
                                .input(Items.IRON_SWORD, PedestalRitualBehavior.TYPE)
                                .input(Items.TNT, PedestalRitualBehavior.TYPE)
                                .input(Items.TNT_MINECART, PedestalRitualBehavior.TYPE)
                                .input(Items.RED_STAINED_GLASS, PedestalRitualBehavior.TYPE)
                        )
                        .duration(300)
                        .daylight(8));

        provider.generic(LucenticsItemRegister.LENSES.get(Colors.BLUE))
                .engraving(b -> b.input(LucenticsItemRegister.LENS_FRAME)
                        .trail(TrailBuilder.create(Colors.SUNLIGHT)
                                .input(Items.COD, PedestalRitualBehavior.TYPE)
                                .input(Items.SALMON, PedestalRitualBehavior.TYPE)
                                .input(Items.KELP, PedestalRitualBehavior.TYPE)
                                .input(Items.WATER_BUCKET, PedestalRitualBehavior.TYPE)
                                .input(ItemTags.BOATS, PedestalRitualBehavior.TYPE)
                                .input(Items.INK_SAC, PedestalRitualBehavior.TYPE)
                                .input(Items.BLUE_STAINED_GLASS, PedestalRitualBehavior.TYPE)
                        )
                        .duration(300)
                        .daylight(8));

        provider.generic(LucenticsItemRegister.LENSES.get(Colors.GREEN))
                .engraving(b -> b.input(LucenticsItemRegister.LENS_FRAME)
                        .trail(TrailBuilder.create(Colors.SUNLIGHT)
                                .input(Items.CRAFTING_TABLE, PedestalRitualBehavior.TYPE)
                                .input(Items.SMITHING_TABLE, PedestalRitualBehavior.TYPE)
                                .input(Items.CARTOGRAPHY_TABLE, PedestalRitualBehavior.TYPE)
                                .input(Items.FLETCHING_TABLE, PedestalRitualBehavior.TYPE)
                                .input(Items.LOOM, PedestalRitualBehavior.TYPE)
                                .input(Items.CRAFTER, PedestalRitualBehavior.TYPE)
                                .input(Items.GREEN_STAINED_GLASS, PedestalRitualBehavior.TYPE)
                        )
                        .duration(300)
                        .daylight(8));

        provider.generic(LucenticsItemRegister.RED_DUSK_BRICK)
                .engraving(b -> b.input(LucenticsItemRegister.DUSK_BRICK)
                        .trail(TrailBuilder.create(Colors.RED)
                                .notConsumeInput(Items.RED_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.RED_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.RED_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.RED_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.RED_DYE, PedestalRitualBehavior.TYPE)
                        )
                        .duration(60));
        provider.generic(LucenticsItemRegister.BLUE_DUSK_BRICK)
                .engraving(b -> b.input(LucenticsItemRegister.DUSK_BRICK)
                        .trail(TrailBuilder.create(Colors.BLUE)
                                .notConsumeInput(Items.BLUE_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.BLUE_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.BLUE_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.BLUE_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.BLUE_DYE, PedestalRitualBehavior.TYPE)
                        )
                        .duration(60));
        provider.generic(LucenticsItemRegister.GREEN_DUSK_BRICK)
                .engraving(b -> b.input(LucenticsItemRegister.DUSK_BRICK)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .notConsumeInput(Items.GREEN_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.GREEN_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.GREEN_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.GREEN_DYE, PedestalRitualBehavior.TYPE)
                                .notConsumeInput(Items.GREEN_DYE, PedestalRitualBehavior.TYPE)
                        )
                        .duration(60));
    }
}
