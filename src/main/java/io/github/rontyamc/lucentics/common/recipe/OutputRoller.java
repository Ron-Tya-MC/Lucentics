package io.github.rontyamc.lucentics.common.recipe;

import io.github.rontyamc.lucentics.common.ThingStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import io.github.rontyamc.lucentics.common.recipe.RecipeArguments.WeightedOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OutputRoller {
    private OutputRoller() {}

    public record RolledOutput(Optional<ItemStack> item, Optional<FluidStack> fluid) {
        public static RolledOutput of(ItemStack item, FluidStack fluid) {
            return new RolledOutput(Optional.of(item), Optional.of(fluid));
        }

        public static RolledOutput of(ItemStack item) {
            return new RolledOutput(Optional.of(item), Optional.empty());
        }

        public static RolledOutput of(FluidStack fluid) {
            return new RolledOutput(Optional.empty(), Optional.of(fluid));
        }

        public static RolledOutput of(ThingStack thing) {
            return thing.isItem() ? of(thing.asItemOrEmpty()) : of(thing.asFluidOrEmpty());
        }
    }

    public static List<RolledOutput> roll(RandomSource random, List<List<WeightedOutput>> groups) {
        List<RolledOutput> results = new ArrayList<>();
        for (List<WeightedOutput> group : groups) {
            WeightedOutput chosen = selectWeighted(random, group);
            if (chosen == null) continue;
            if (chosen.probability() < 1.0 && random.nextFloat() >= chosen.probability()) continue;

            chosen.content().ifLeft(item -> results.add(RolledOutput.of(item.roll(random))));
            chosen.content().ifRight(fluid -> results.add(RolledOutput.of(fluid.roll(random))));
        }
        return results;
    }

    private static WeightedOutput selectWeighted(RandomSource random, List<WeightedOutput> group) {
        if (group.isEmpty()) return null;
        if (group.size() == 1) return group.getFirst();

        int total = group.stream().mapToInt(WeightedOutput::weight).sum();
        if (total <= 0) return null;
        int roll = random.nextInt(total);
        int current = 0;
        for (WeightedOutput candidate : group) {
            current += candidate.weight();
            if (roll < current) return candidate;
        }
        return group.getLast();
    }
}
