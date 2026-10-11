package io.github.rontyamc.lucentics.client.patchouli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ImagePage extends BasePage<ImagePage> {
    List<ResourceLocation> images = new ArrayList<>();
    String titleKey = "";
    String textKey = "";
    boolean border = false;

    public ImagePage() {
        this.type = ResourceLocation.fromNamespaceAndPath("patchouli", "image");
    }

    public ImagePage addImage(ResourceLocation image) {
        this.images.add(image);
        return this;
    }

    public ImagePage border(boolean border) {
        this.border = border;
        return this;
    }

    public ImagePage setTitle(String key) {
        this.titleKey = key;
        return this;
    }

    public ImagePage setText(String key) {
        this.textKey = key;
        return this;
    }

    @Override
    public JsonObject toJson(String prefix, JsonObject json) {
        super.toJson(prefix, json);

        JsonArray array = new JsonArray();
        for (ResourceLocation image : images) array.add(image.toString());
        json.add("images", array);

        if (titleKey != null && !titleKey.isEmpty()) json.addProperty("title", key(prefix, titleKey, "title"));
        if (border) json.addProperty("border", true);
        if (textKey != null && !textKey.isEmpty()) json.addProperty("text", key(prefix, textKey, "text"));
        return json;
    }
}
