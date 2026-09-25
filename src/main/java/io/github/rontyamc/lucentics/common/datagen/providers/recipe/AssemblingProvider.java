package io.github.rontyamc.lucentics.common.datagen.providers.recipe;

import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBehavior;
import io.github.rontyamc.lucentics.blocks.tank.TankBehavior;
import io.github.rontyamc.lucentics.common.datagen.builders.InputSpec;
import io.github.rontyamc.lucentics.common.datagen.builders.TrailBuilder;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsFluidRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister.LucenticsITags;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class AssemblingProvider {
    public AssemblingProvider() {}

    protected static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.leaveFolder();

        provider.generic(LucenticsItemRegister.IRON_PLATE)
                .assembling(b -> b
                        .input(Items.IRON_INGOT)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(LucenticsITags.HAMMERS.tag).damageItem(1).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(60)
                );
        provider.generic(LucenticsItemRegister.GOLD_PLATE)
                .assembling(b -> b
                        .input(Items.GOLD_INGOT)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(LucenticsITags.HAMMERS.tag).damageItem(1).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(50)
                );

        provider.generic(LucenticsItemRegister.PRISM_BALL)
                .assembling(b -> b
                        .input(LucenticsItemRegister.DAWN_CRYSTAL, 2)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(LucenticsItemRegister.GLASS_SHERD, 2).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.DAWNSTONE_DUST).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.DAWNSTONE_DUST).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.DAWNSTONE_DUST).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.DAWNSTONE_DUST).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.GLASS_SHERD, 2).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(200)
                        .daylight(12)
                );

        provider.generic()
                .assembling(b -> b
                        .input(LucenticsItemRegister.GOLD_PLATE)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(Items.SHEARS).damageItem(3).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .output(LucenticsItemRegister.GOLD_WIRE, 6)
                        .duration(80)
                );

        provider.generic(LucenticsItemRegister.MOLD_RING)
                .assembling(b -> b
                        .input(LucenticsItemRegister.IRON_PLATE, 2)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(LucenticsITags.HAMMERS.tag).damageItem(5).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.FLOWER_POT).notConsume(true).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(160)
                );
        provider.generic(LucenticsItemRegister.MOLD_INGOT)
                .assembling(b -> b
                        .input(LucenticsItemRegister.IRON_PLATE, 2)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(LucenticsITags.HAMMERS.tag).damageItem(5).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsITags.INGOTS.tag).notConsume(true).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(160)
                );
        provider.generic(LucenticsItemRegister.MOLD_BOX)
                .assembling(b -> b
                        .input(LucenticsItemRegister.IRON_PLATE, 5)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(LucenticsITags.HAMMERS.tag).damageItem(12).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsBlockRegister.DAWNSTONE).notConsume(true).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(160)
                );

        provider.generic(LucenticsItemRegister.LIGHT_COPPER_RING, 2)
                .assembling(b -> b
                        .input(LucenticsItemRegister.LIGHT_COPPER)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(LucenticsITags.HAMMERS.tag).damageItem(8).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.MOLD_RING).notConsume(true).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(100)
                );

        provider.generic(LucenticsItemRegister.FOCUS_RING)
                .assembling(b -> b
                        .input(LucenticsItemRegister.PRISM_BALL)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(LucenticsItemRegister.GOLD_WIRE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.GOLD_WIRE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.LIGHT_COPPER_RING, 2).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.GOLD_WIRE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.GOLD_WIRE).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(400)
                );

        provider.generic(LucenticsBlockRegister.PRISM_BLANK)
                .assembling(b -> b
                        .input(Blocks.GLASS)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(LucenticsItemRegister.DAWNSTONE_DUST).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.DAWNSTONE_DUST).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.DAWNSTONE_DUST).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(40)
                        .daylight(8)
                );

        provider.generic(LucenticsBlockRegister.PEDESTAL_RITUAL)
                .assembling(b -> b
                        .input(Items.GOLD_INGOT)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(LucenticsBlockRegister.DAWNSTONE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsBlockRegister.DAWNSTONE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsBlockRegister.DAWNSTONE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsBlockRegister.DAWNSTONE).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(60)
                        .daylight(8)
                );

        provider.generic(LucenticsBlockRegister.DUSK_BRICKS, 2)
                .assembling(b -> b
                        .input(Items.BRICKS)
                        .trail(TrailBuilder.create(Colors.SUNLIGHT)
                                .input(InputSpec.fluid(LucenticsFluidRegister.CLAY_WATER.get().getSource(), 1000).requiredType(TankBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.MOLD_BOX).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(150)
                        .daylight(7)
                );
        provider.generic(LucenticsItemRegister.DUSK_BRICK, 2)
                .assembling(b -> b
                        .input(Items.BRICK)
                        .trail(TrailBuilder.create(Colors.SUNLIGHT)
                                .input(InputSpec.fluid(LucenticsFluidRegister.CLAY_WATER.get().getSource(), 250).requiredType(TankBehavior.TYPE))
                                .input(InputSpec.item(LucenticsItemRegister.MOLD_INGOT).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(40)
                        .daylight(7)
                );

        provider.generic(LucenticsBlockRegister.PRISM_DYEING)
                .assembling(b -> b.input(LucenticsBlockRegister.PRISM_BLANK)
                        .trail(TrailBuilder.create(Colors.GREEN)
                                .input(InputSpec.item(Items.LAPIS_LAZULI, 4).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.fluid(LucenticsFluidRegister.DYE_LIQUIDS.get(Colors.GREEN).get().getSource(), 8000).requiredType(TankBehavior.TYPE))
                                .input(InputSpec.item(LucenticsITags.DUSTS_DIAMOND.tag).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(LucenticsITags.DYES.tag, 4).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .duration(400)
                        .daylight(12));
    }
}
