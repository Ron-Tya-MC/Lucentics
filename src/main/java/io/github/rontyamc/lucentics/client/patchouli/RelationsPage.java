package io.github.rontyamc.lucentics.client.patchouli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.rontyamc.lucentics.Lucentics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class RelationsPage extends BasePage<RelationsPage> {
    List<String> entries = new ArrayList<>();
    String titleKey = "";
    String textKey = "";

    public RelationsPage() {
        this.type = ResourceLocation.fromNamespaceAndPath("patchouli", "relations");
    }

    public RelationsPage addEntry(String path) {
        this.entries.add(path);
        return this;
    }

    public RelationsPage addEntry(ResourceLocation category, ResourceLocation entry) {
        return addEntry(category.toString() + "/" + entry.getPath());
    }

    public RelationsPage addEntry(ResourceLocation category, String entry) {
        return addEntry(category, Lucentics.defaultLocation(entry));
    }

    public RelationsPage addEntry(String category, ResourceLocation entry) {
        return addEntry(Lucentics.defaultLocation(category), entry);
    }

    public RelationsPage addEntry(String category, String entry) {
        return addEntry(Lucentics.defaultLocation(category), Lucentics.defaultLocation(entry));
    }

    public RelationsPage setTitle(String key) {
        this.titleKey = key;
        return this;
    }

    public RelationsPage setText(String key) {
        this.textKey = key;
        return this;
    }

    @Override
    public JsonObject toJson(String prefix, JsonObject json) {
        super.toJson(prefix, json);

        JsonArray array = new JsonArray();
        for (String entry : entries) array.add(entry);
        json.add("entries", array);

        if (titleKey != null && !titleKey.isEmpty()) json.addProperty("title", key(prefix, titleKey, "title"));
        else json.addProperty("title", "patchouli.lucentics.common.relations");
        if (textKey != null && !textKey.isEmpty()) json.addProperty("text", key(prefix, textKey, "text"));
        return json;
    }
}
