package io.github.rontyamc.lucentics.registers;

import io.github.rontyamc.lucentics.Lucentics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.Locale;

public class LucenticsTagRegister {
    public enum TagLocation {
        LUCENTICS(Lucentics.MOD_ID),
        COMMON("c");

        public final String namespace;

        TagLocation(String namespace) {
            this.namespace = namespace;
        }

        public ResourceLocation path(String id) {
            return ResourceLocation.fromNamespaceAndPath(this.namespace, id);
        }

        public ResourceLocation path(Enum<?> entry) {
            return this.path(entry.name().toLowerCase(Locale.ROOT));
        }

        public ResourceLocation path(Enum<?> entry, String specify) {
            return specify.isEmpty() ? this.path(entry) : this.path(specify);
        }
    }

    public enum LucenticsITags {
        HAMMERS,
        TOOLS(TagLocation.COMMON),
        MINING_TOOLS(TagLocation.COMMON, "tools/mining_tool"),
        ENCHANTABLES(TagLocation.COMMON),
        INGOTS(TagLocation.COMMON),
        DUSK_BRICKS,
        LIGHT_COPPERS,
        LENSES;

        public final TagKey<Item> tag;

        LucenticsITags(TagLocation location, String specify) {
            this.tag = TagKey.create(Registries.ITEM, location.path(this, specify));
        }

        LucenticsITags(TagLocation location) {
            this(location, "");
        }

        LucenticsITags() {
            this(TagLocation.LUCENTICS);
        }
    }

    public enum LucenticsBTags {
        DUSK_BRICKS,
        LIGHT_COPPER_BLOCKS,
        PRISMS,
        PEDESTALS;

        public final TagKey<Block> tag;

        LucenticsBTags(TagLocation location, String specify) {
            this.tag = TagKey.create(Registries.BLOCK, location.path(this, specify));
        }

        LucenticsBTags(TagLocation location) {
            this(location, "");
        }

        LucenticsBTags() {
            this(TagLocation.LUCENTICS);
        }
    }

    public enum LucenticsFTags {
        DYE_LIQUIDS,
        COLOQUIDS;

        public final TagKey<Fluid> tag;

        LucenticsFTags(TagLocation location, String specify) {
            this.tag = TagKey.create(Registries.FLUID, location.path(this, specify));
        }

        LucenticsFTags(TagLocation location) {
            this(location, "");
        }

        LucenticsFTags() {
            this(TagLocation.LUCENTICS);
        }
    }
}
