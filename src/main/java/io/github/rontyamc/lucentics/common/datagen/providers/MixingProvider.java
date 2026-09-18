package io.github.rontyamc.lucentics.common.datagen.providers;

import io.github.rontyamc.lucentics.common.datagen.builders.InputSpec;
import io.github.rontyamc.lucentics.common.datagen.builders.TrailBuilder;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsFluidRegister;
import net.minecraft.data.recipes.RecipeOutput;
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
                                    .input(InputSpec.item(Colors.cTag(color), 1))
                            )
                            .output(LucenticsFluidRegister.DYE_LIQUIDS.get(color).get().getSource(), FluidType.BUCKET_VOLUME)
                            .duration(80));
        }
    }
}
