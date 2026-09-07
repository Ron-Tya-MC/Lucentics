package io.github.rontyamc.lucentics.registers;

import io.github.rontyamc.lucentics.Lucentics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Locale;

public class LucenticsTagRegister {
    public enum TagLocation {
        LUCENTICS(Lucentics.MOD_ID),
        COMMON("c");

        public final String namespace;

        private TagLocation(String namespace) {
            this.namespace = namespace;
        }

        public ResourceLocation path(String id) {
            return ResourceLocation.fromNamespaceAndPath(this.namespace, id);
        }

        public ResourceLocation path(Enum<?> entry) {
            return this.path(entry.name().toLowerCase(Locale.ROOT));
        }
    }

    public enum LItemTags {
        HAMMERS;

        public final TagKey<Item> tag;

        LItemTags(TagLocation location) {
            this.tag = TagKey.create(Registries.ITEM, location.path(this));
        }

        LItemTags() {
            this(TagLocation.LUCENTICS);
        }
    }
}
