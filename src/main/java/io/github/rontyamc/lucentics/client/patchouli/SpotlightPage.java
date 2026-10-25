package io.github.rontyamc.lucentics.client.patchouli;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpotlightPage extends BasePage<SpotlightPage> {
    List<ItemStackFormat> items = new ArrayList<>();
    String titleKey = "";
    boolean linkRecipe = false;
    String textKey = "";

    public SpotlightPage(ItemStackFormat format) {
        this.items.add(format);
        this.type = ResourceLocation.fromNamespaceAndPath("patchouli", "spotlight");
    }

    public SpotlightPage(Item item) {
        this(new ItemStackFormat(item));
    }

    public SpotlightPage(List<ItemStack> items) {
        this(new ItemStackFormat(items.getFirst().getItem(), items.getFirst().getCount()));
        this.addItems(items.subList(1, items.size() - 1));
    }

    public SpotlightPage(TagKey<Item> item) {
        this(new ItemStackFormat(item));
    }

    public SpotlightPage(Item item, int count) {
        this(new ItemStackFormat(item, count));
    }

    public SpotlightPage(TagKey<Item> item, int count) {
        this(new ItemStackFormat(item, count));
    }

    public SpotlightPage addItem(ItemStackFormat format) {
        this.items.add(format);
        return this;
    }

    public SpotlightPage addItem(Item item) {
        return addItem(new ItemStackFormat(item));
    }

    public SpotlightPage addItem(TagKey<Item> item) {
        return addItem(new ItemStackFormat(item));
    }

    public SpotlightPage addItem(Item item, int count) {
        return addItem(new ItemStackFormat(item, count));
    }

    public SpotlightPage addItem(TagKey<Item> item, int count) {
        return addItem(new ItemStackFormat(item, count));
    }

    public SpotlightPage addItems(List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            this.items.add(new ItemStackFormat(stack.getItem(), stack.getCount()));
        }
        return this;
    }

    public SpotlightPage setTitle(String key) {
        this.titleKey = key;
        return this;
    }

    public SpotlightPage linkRecipe(boolean linkRecipe) {
        this.linkRecipe = linkRecipe;
        return this;
    }

    public SpotlightPage setText(String key) {
        this.textKey = key;
        return this;
    }

    @Override
    public JsonObject toJson(String prefix, JsonObject json) {
        super.toJson(prefix, json);

        String itemText = items.stream().map(ItemStackFormat::toString).collect(Collectors.joining(","));

        json.addProperty("item", itemText);
        if (titleKey != null && !titleKey.isEmpty()) json.addProperty("title", key(prefix, titleKey, "title"));
        if (linkRecipe) json.addProperty("linkRecipe", true);
        if (textKey != null && !textKey.isEmpty()) json.addProperty("text", key(prefix, textKey, "text"));
        return json;
    }
}
