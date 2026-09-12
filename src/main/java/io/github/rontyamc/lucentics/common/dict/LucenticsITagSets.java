package io.github.rontyamc.lucentics.common.dict;

import io.github.rontyamc.lucentics.common.util.tag_utils.ItemTagSet;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister;
import net.minecraft.tags.ItemTags;

import java.util.List;

public final class LucenticsITagSets {
    private LucenticsITagSets() {
    }

    public static final ItemTagSet ENCHANTABLE_COMMON =
            new ItemTagSet(ItemTags.VANISHING_ENCHANTABLE, LucenticsTagRegister.LucenticsITags.ENCHANTABLES.tag);

    public static final ItemTagSet ENCHANTABLE_MININGS =
            new ItemTagSet(ENCHANTABLE_COMMON, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE);

    public static final ItemTagSet TOOLS_MINING =
            new ItemTagSet(LucenticsTagRegister.LucenticsITags.TOOLS.tag, LucenticsTagRegister.LucenticsITags.MINING_TOOLS.tag, ItemTags.BREAKS_DECORATED_POTS);

    public static final ItemTagSet PICKAXE_LIKE =
            new ItemTagSet(List.of(ENCHANTABLE_MININGS, TOOLS_MINING), ItemTags.CLUSTER_MAX_HARVESTABLES);

    public static final ItemTagSet PICKAXE =
            new ItemTagSet(PICKAXE_LIKE, ItemTags.PICKAXES);
}
