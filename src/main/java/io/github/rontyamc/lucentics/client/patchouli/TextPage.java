package io.github.rontyamc.lucentics.client.patchouli;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class TextPage extends BasePage<TextPage> {
    String key;
    boolean titled = false;

    public TextPage(String key) {
        this.key = key;
        this.type = ResourceLocation.fromNamespaceAndPath("patchouli", "text");
    }

    public TextPage titled(boolean titled) {
        this.titled = titled;
        return this;
    }

    @Override
    public JsonObject toJson(String prefix, JsonObject json) {
        super.toJson(prefix, json);

        json.addProperty("text", key(prefix, key, "text"));
        if (titled) json.addProperty("title", key(prefix, key, "title"));
        return json;
    }
}
