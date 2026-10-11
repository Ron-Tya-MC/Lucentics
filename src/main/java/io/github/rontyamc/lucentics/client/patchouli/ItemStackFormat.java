package io.github.rontyamc.lucentics.client.patchouli;

import com.mojang.datafixers.util.Either;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class ItemStackFormat {
    private Either<Item, TagKey<Item>> item;
    private int count = 1;
    @Nullable
    private PatchedDataComponentMap components = null;

    public ItemStackFormat(Item item) {
        this.item = Either.left(item);
    }

    public ItemStackFormat(TagKey<Item> item) {
        this.item = Either.right(item);
    }

    public ItemStackFormat(Item item, int count) {
        this.item = Either.left(item);
        this.count = count;
    }

    public ItemStackFormat(TagKey<Item> item, int count) {
        this.item = Either.right(item);
        this.count = count;
    }

    public <T> void setComponent(DataComponentType<? super T> component, T value) {
        if (this.components == null) this.item.map(
                item -> this.components = new PatchedDataComponentMap(item.components()),
                tag -> this.components = new PatchedDataComponentMap(Items.AIR.components())
        );
        else this.components.set(component, value);
    }

    public String toString() {
        String itemPath = (item.right().isPresent() ? "tag:" : "") + item.map(ItemUtil::getId, TagKey::location).toString();
        String countText = count == 1 ? "" : "#" + count;
        String nbtText = components == null ? "" : components.toString();

        return itemPath + countText + nbtText;
    }
}
