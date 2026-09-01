package io.github.rontyamc.lucentics.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class FluidUtilities {
    public static ResourceLocation getId(Supplier<? extends Fluid> output) {
        return BuiltInRegistries.FLUID.getKey(output.get());
    }
}
