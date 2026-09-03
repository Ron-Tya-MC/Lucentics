package io.github.rontyamc.lucentics.common.datagen.providers;

import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBehavior;
import io.github.rontyamc.lucentics.common.datagen.builders.TrailBuilder;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import net.minecraft.data.recipes.RecipeOutput;

public class MillingProvider {
    public MillingProvider() {}

    protected static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.leaveFolder();

        provider.generic(LucenticsItemRegister.DAWNSTONE_DUST)
                .milling(b -> b.input(LucenticsBlockRegister.DAWNSTONE)
                        .trail(TrailBuilder.create(Colors.RED)
                                .notConsumeInput(LucenticsItemRegister.COPPER_HAMMER, PedestalRitualBehavior.TYPE)
                        )
                        .duration(20)
                );
    }
}
