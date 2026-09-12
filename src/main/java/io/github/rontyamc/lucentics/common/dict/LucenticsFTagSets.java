package io.github.rontyamc.lucentics.common.dict;

import io.github.rontyamc.lucentics.common.util.tag_utils.FluidTagSet;
import net.minecraft.tags.FluidTags;
import net.neoforged.neoforge.common.Tags;

public final class LucenticsFTagSets {
    private LucenticsFTagSets() {}

    public static final FluidTagSet WATER =
            new FluidTagSet(FluidTags.WATER, Tags.Fluids.WATER);
}
