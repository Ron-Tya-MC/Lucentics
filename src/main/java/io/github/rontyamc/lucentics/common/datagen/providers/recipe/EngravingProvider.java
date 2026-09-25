package io.github.rontyamc.lucentics.common.datagen.providers.recipe;

import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBehavior;
import io.github.rontyamc.lucentics.blocks.tank.TankBehavior;
import io.github.rontyamc.lucentics.common.datagen.builders.InputSpec;
import io.github.rontyamc.lucentics.common.datagen.builders.TrailBuilder;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsFluidRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister.LucenticsITags;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

public class EngravingProvider {
    public EngravingProvider() {}

    protected static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.leaveFolder();

        provider.generic(LucenticsItemRegister.LIGHT_COPPER)
                .engraving(b -> b.input(Items.COPPER_INGOT)
                        .trail(TrailBuilder.create(Colors.SUNLIGHT)
                                .input(InputSpec.item(Items.IRON_NUGGET).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.GOLD_NUGGET).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.GOLD_NUGGET).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.GOLD_NUGGET).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.IRON_NUGGET).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                        )
                        .duration(100)
                        .daylight(8));

        provider.generic(LucenticsItemRegister.LENSES.get(Colors.RED))
                .engraving(b -> b.input(LucenticsItemRegister.LENS_FRAME)
                        .trail(TrailBuilder.create(Colors.SUNLIGHT)
                                .input(InputSpec.item(Items.IRON_PICKAXE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.IRON_AXE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.IRON_SHOVEL).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.IRON_SWORD).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.TNT).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.TNT_MINECART).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.RED_STAINED_GLASS).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(300)
                        .daylight(8));

        provider.generic(LucenticsItemRegister.LENSES.get(Colors.BLUE))
                .engraving(b -> b.input(LucenticsItemRegister.LENS_FRAME)
                        .trail(TrailBuilder.create(Colors.SUNLIGHT)
                                .input(InputSpec.item(Items.COD).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.SALMON).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.KELP).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.WATER_BUCKET).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(ItemTags.BOATS).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.INK_SAC).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.BLUE_STAINED_GLASS).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(300)
                        .daylight(8));

        provider.generic(LucenticsItemRegister.LENSES.get(Colors.GREEN))
                .engraving(b -> b.input(LucenticsItemRegister.LENS_FRAME)
                        .trail(TrailBuilder.create(Colors.SUNLIGHT)
                                .input(InputSpec.item(Items.CRAFTING_TABLE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.SMITHING_TABLE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.CARTOGRAPHY_TABLE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.FLETCHING_TABLE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.LOOM).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.CRAFTER).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.GREEN_STAINED_GLASS).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(300)
                        .daylight(8));

        provider.generic(LucenticsItemRegister.RED_DUSK_BRICK)
                .engraving(b -> b.input(LucenticsItemRegister.DUSK_BRICK)
                        .trail(TrailBuilder.create(Colors.RED)
                                .input(InputSpec.item(Items.RED_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.RED_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.RED_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.RED_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.RED_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                        )
                        .duration(60)
                        .daylight(7));
        provider.generic(LucenticsItemRegister.BLUE_DUSK_BRICK)
                .engraving(b -> b.input(LucenticsItemRegister.DUSK_BRICK)
                        .trail(TrailBuilder.create(Colors.BLUE)
                                .input(InputSpec.item(Items.BLUE_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.BLUE_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.BLUE_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.BLUE_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.BLUE_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                        )
                        .duration(60)
                        .daylight(7));
        provider.generic(LucenticsItemRegister.GREEN_DUSK_BRICK)
                .engraving(b -> b.input(LucenticsItemRegister.DUSK_BRICK)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(Items.GREEN_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.GREEN_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.GREEN_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.GREEN_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                                .input(InputSpec.item(Items.GREEN_DYE).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                        )
                        .duration(60)
                        .daylight(7));

        provider.generic(LucenticsItemRegister.DAWN_CRYSTAL)
                .engraving(b -> b.input(LucenticsItemRegister.DAWNSTONE_DUST)
                        .trail(TrailBuilder.create(Colors.SUNLIGHT)
                                .input(InputSpec.item(LucenticsItemRegister.DAWNSTONE_DUST, 2).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.fluid(Fluids.LAVA, 50).requiredType(TankBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.DAWNSTONE_DUST, 2).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.fluid(Fluids.LAVA, 50).requiredType(TankBehavior.TYPE))
                                .input(InputSpec.item(LucenticsITags.DUSTS_DIAMOND.tag).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                        )
                        .duration(120)
                        .daylight(10));

        provider.generic(LucenticsItemRegister.GLIMMER_IRON, 2)
                .engraving(b -> b.input(Items.IRON_INGOT, 2)
                        .trail(TrailBuilder.create(Colors.RED)
                                .input(InputSpec.item(LucenticsItemRegister.DUSK_BRICK_DUST).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsITags.DUSTS_OBSIDIAN.tag).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                        )
                        .trail(TrailBuilder.create(Colors.BLUE)
                                .input(InputSpec.fluid(LucenticsFluidRegister.DYE_LIQUIDS.get(Colors.YELLOW).get().getSource(), 250).requiredType(TankBehavior.TYPE))
                                .input(InputSpec.fluid(LucenticsFluidRegister.PATINA_LIQUID.get().getSource(), 250).requiredType(TankBehavior.TYPE))
                        )
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(LucenticsItemRegister.DAWN_CRYSTAL).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.FOCUS_RING).requiredType(PedestalRitualBehavior.TYPE).notConsume(true))
                        )
                        .duration(240)
                        .daylight(9));
    }
}
