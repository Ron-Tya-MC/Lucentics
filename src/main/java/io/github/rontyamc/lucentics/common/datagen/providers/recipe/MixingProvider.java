package io.github.rontyamc.lucentics.common.datagen.providers.recipe;

import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBehavior;
import io.github.rontyamc.lucentics.common.datagen.builders.InputSpec;
import io.github.rontyamc.lucentics.common.datagen.builders.TrailBuilder;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsFluidRegister;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister.LucenticsITags;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;

public class MixingProvider {
    public MixingProvider() {}

    protected static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.leaveFolder();

        for (Colors color : Colors.get16Colors()) {
            provider.generic()
                    .path(color.getName() + "_dye_liquid")
                    .mixing(b -> b
                            .input(Fluids.WATER, 1000)
                            .trail(TrailBuilder.create(Colors.BLUE)
                                    .input(InputSpec.item(Colors.cTag(color), 1).requiredType(PedestalRitualBehavior.TYPE))
                            )
                            .output(LucenticsFluidRegister.DYE_LIQUIDS.get(color).get().getSource(), FluidType.BUCKET_VOLUME)
                            .duration(80));
        }

        provider.generic()
                .mixing(b -> b
                        .input(Fluids.WATER, 1000)
                        .trail(TrailBuilder.create(Colors.BLUE)
                                .input(InputSpec.item(Items.CLAY_BALL).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .output(LucenticsFluidRegister.CLAY_WATER.get().getSource(), 500)
                        .duration(100));
        provider.generic()
                .suffix("_from_block")
                .mixing(b -> b
                        .input(Fluids.WATER, 4000)
                        .trail(TrailBuilder.create(Colors.BLUE)
                                .input(InputSpec.item(Blocks.CLAY).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .output(LucenticsFluidRegister.CLAY_WATER.get().getSource(), 2000)
                        .duration(300));

        provider.generic()
                .mixing(b -> b
                        .input(Fluids.WATER, 4000)
                        .trail(TrailBuilder.create(Colors.BLUE)
                                .input(InputSpec.item(LucenticsITags.DUSTS_COPPER.tag).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.FERMENTED_SPIDER_EYE).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .output(LucenticsFluidRegister.PATINA_LIQUID.get().getSource(), 4000)
                        .duration(100));
        provider.generic()
                .suffix("_ferment")
                .mixing(b -> b
                        .input(Fluids.WATER, 4000)
                        .trail(TrailBuilder.create(Colors.BLUE)
                                .input(InputSpec.item(LucenticsITags.DUSTS_COPPER.tag).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.SPIDER_EYE).requiredType(PedestalRitualBehavior.TYPE))
                                .input(InputSpec.item(Items.SUGAR).requiredType(PedestalRitualBehavior.TYPE))
                        )
                        .output(LucenticsFluidRegister.PATINA_LIQUID.get().getSource(), 4000)
                        .duration(300));
    }
}
