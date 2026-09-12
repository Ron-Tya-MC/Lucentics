package io.github.rontyamc.lucentics.common.util;

import io.github.rontyamc.lucentics.common.FluidSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Supplier;

public final class FluidUtil {
    public static boolean isSameFluid(FluidStack stackA, FluidStack stackB, boolean allowEmpty) {
        return allowEmpty ? stackA.is(stackB.getFluid()) : !stackB.isEmpty() && stackA.is(stackB.getFluid());
    }

    public static void stackOrAppend(List<FluidStack> container, FluidStack addStack, int capacity) {
        FluidStack newStack = addStack.copy();
        if (newStack.isEmpty()) return;

        for (FluidStack containStack : container) {
            if (!isSameFluid(containStack, newStack, false)) continue;

            int remainingSpace = capacity - containStack.getAmount();
            if (remainingSpace <= 0) continue;

            int inserted = Math.min(newStack.getAmount(), remainingSpace);
            containStack.grow(inserted);
            newStack.shrink(inserted);
        }

        if (!newStack.isEmpty()) {
            container.add(newStack);
        }
    }

    public static void stackOrAppendSlot(List<FluidSlot> container, FluidStack addStack, int capacity) {
        FluidStack newStack = addStack.copy();
        if (newStack.isEmpty()) return;

        for (FluidSlot slot : container) {
            if (!isSameFluid(slot.getContent(), newStack, false)) continue;

            int remainingSpace = capacity - slot.getContent().getAmount();
            if (remainingSpace <= 0) continue;

            int inserted = Math.min(newStack.getAmount(), remainingSpace);
            slot.getContent().grow(inserted);
            newStack.shrink(inserted);
        }

        if (!newStack.isEmpty()) {
            container.add(FluidSlot.of(newStack, capacity));
        }
    }

    public static ResourceLocation getId(Supplier<? extends Fluid> output) {
        return BuiltInRegistries.FLUID.getKey(output.get());
    }
}
