package io.github.rontyamc.lucentics.common.dict;

import io.github.rontyamc.lucentics.common.util.tag_utils.ItemTagSet;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister.LucenticsITags;
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

    public static final ItemTagSet LIGHT_COPPER =
            new ItemTagSet(LucenticsITags.LIGHT_COPPERS.tag, LucenticsITags.INGOTS.tag);
    public static final ItemTagSet GLIMMER_IRON =
            new ItemTagSet(LucenticsITags.GLIMMER_IRONS.tag, LucenticsITags.INGOTS.tag);

    public static final ItemTagSet HAMMER_IRON =
            new ItemTagSet(LucenticsITags.HAMMERS.tag, LucenticsITags.HAMMER_IRON_TIERS.tag);
    public static final ItemTagSet HAMMER_DIAMOND =
            new ItemTagSet(LucenticsITags.HAMMERS.tag, LucenticsITags.HAMMER_IRON_TIERS.tag, LucenticsITags.HAMMER_DIAMOND_TIERS.tag);

    public static final ItemTagSet PLATE_IRON =
            new ItemTagSet(LucenticsITags.PLATES.tag, LucenticsITags.PLATES_IRON.tag);
    public static final ItemTagSet PLATE_GOLD =
            new ItemTagSet(LucenticsITags.PLATES.tag, LucenticsITags.PLATES_GOLD.tag, ItemTags.PIGLIN_LOVED);

    public static final ItemTagSet DUST_COPPER =
            new ItemTagSet(LucenticsITags.DUSTS.tag, LucenticsITags.DUSTS_COPPER.tag);
    public static final ItemTagSet DUST_IRON =
            new ItemTagSet(LucenticsITags.DUSTS.tag, LucenticsITags.DUSTS_IRON.tag);
    public static final ItemTagSet DUST_GOLD =
            new ItemTagSet(LucenticsITags.DUSTS.tag, LucenticsITags.DUSTS_GOLD.tag);
    public static final ItemTagSet DUST_DIAMOND =
            new ItemTagSet(LucenticsITags.DUSTS.tag, LucenticsITags.DUSTS_DIAMOND.tag);
    public static final ItemTagSet DUST_OBSIDIAN =
            new ItemTagSet(LucenticsITags.DUSTS.tag, LucenticsITags.DUSTS_OBSIDIAN.tag);
}
