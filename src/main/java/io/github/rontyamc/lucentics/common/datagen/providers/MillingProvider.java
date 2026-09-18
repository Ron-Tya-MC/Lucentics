package io.github.rontyamc.lucentics.common.datagen.providers;

import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBehavior;
import io.github.rontyamc.lucentics.common.datagen.builders.InputSpec;
import io.github.rontyamc.lucentics.common.datagen.builders.OutputSpec;
import io.github.rontyamc.lucentics.common.datagen.builders.TrailBuilder;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister.LucenticsITags;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class MillingProvider {
    public MillingProvider() {}

    protected static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.leaveFolder();

        provider.generic(LucenticsItemRegister.DAWNSTONE_DUST)
                .milling(b -> b.input(LucenticsBlockRegister.DAWNSTONE)
                        .trail(TrailBuilder.create(Colors.RED)
                                .input(InputSpec.item(LucenticsITags.HAMMERS.tag)
                                        .requiredType(PedestalRitualBehavior.TYPE)
                                        .damageItem(1))
                        )
                        .duration(20)
                );

        provider.generic(LucenticsItemRegister.COPPER_DUST)
                .milling(b -> b.input(Items.RAW_COPPER)
                        .output(OutputSpec.of(LucenticsItemRegister.COPPER_DUST).probability(0.3f))
                        .trail(TrailBuilder.create(Colors.RED)
                                .input(InputSpec.item(LucenticsITags.HAMMERS.tag)
                                        .requiredType(PedestalRitualBehavior.TYPE)
                                        .damageItem(1))
                        )
                        .duration(60)
                );
        provider.generic()
                .suffix("from_block")
                .milling(b -> b.input(Blocks.RAW_COPPER_BLOCK)
                        .output(OutputSpec.of(LucenticsItemRegister.COPPER_DUST).count(UniformInt.of(9,14)))
                        .trail(TrailBuilder.create(Colors.RED)
                                .input(InputSpec.item(LucenticsITags.HAMMERS.tag)
                                        .requiredType(PedestalRitualBehavior.TYPE)
                                        .damageItem(9))
                        )
                        .duration(500)
                );
    }
}
