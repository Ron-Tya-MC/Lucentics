package io.github.rontyamc.lucentics.common.util;

import io.github.rontyamc.lucentics.common.FluidSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class FluidUtil {
    public static boolean isSameFluid(FluidStack stackA, FluidStack stackB, boolean allowEmpty) {
        return allowEmpty ? stackA.is(stackB.getFluid()) : !stackB.isEmpty() && stackA.is(stackB.getFluid());
    }

    public static List<FluidSlot> stackOrAppend(List<FluidSlot> container, FluidStack addStack, int capacity) {
        FluidStack newStack = addStack.copy();
        List<FluidSlot> newContainer = deepCopyOfSlot(container);

        if (newStack.isEmpty()) return newContainer;

        for (FluidSlot slot : newContainer) {
            if (!isSameFluid(slot.getContent(), newStack, false)) continue;

            int remainingSpace = capacity - slot.getContent().getAmount();
            if (remainingSpace <= 0) continue;

            int inserted = Math.min(newStack.getAmount(), remainingSpace);
            slot.getContent().grow(inserted);
            newStack.shrink(inserted);
        }

        if (!newStack.isEmpty()) {
            newContainer.add(FluidSlot.of(newStack, capacity));
        }

        return newContainer;
    }

    public static List<FluidStack> deepCopyOfStack(List<FluidStack> origin) {
        return origin.stream().map(fluid -> fluid != null ? fluid.copy() : null).collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<FluidSlot> deepCopyOfSlot(List<FluidSlot> origin) {
        return origin.stream().map(fluid -> fluid != null ? FluidSlot.of(fluid.getContent().copy(), fluid.getCapacity()) : null).collect(Collectors.toCollection(ArrayList::new));
    }

    public static ResourceLocation getId(Supplier<? extends Fluid> output) {
        return BuiltInRegistries.FLUID.getKey(output.get());
    }

    public static boolean isIdInTag(ResourceLocation id, TagKey<Fluid> tagKey, Level level) {
        return level.registryAccess().lookup(Registries.FLUID)
                .flatMap(registry -> registry.get(ResourceKey.create(Registries.FLUID, id)))
                .map(holder -> holder.is(tagKey))
                .orElse(false);
    }

    public record FluidMergeResult(FluidStack merged, FluidStack leftover) {
        public static FluidMergeResult of(FluidStack merged, FluidStack leftover) {
            return new FluidMergeResult(merged, leftover);
        }
    }

    public static FluidMergeResult merge(FluidStack base, FluidStack add) {
        if (add.isEmpty()) return FluidMergeResult.of(base.copy(), FluidStack.EMPTY);
        if (!isSameFluid(base, add, false)) return FluidMergeResult.of(base.copy(), add.copy());

        return FluidMergeResult.of(base.copyWithAmount(base.getAmount() + add.getAmount()), FluidStack.EMPTY);
    }
}
