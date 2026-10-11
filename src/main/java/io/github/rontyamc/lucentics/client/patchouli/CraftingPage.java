package io.github.rontyamc.lucentics.client.patchouli;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class CraftingPage extends BasePage<CraftingPage> {
    ResourceLocation recipe;
    @Nullable
    ResourceLocation recipe2 = null;
    String titleKey = "";
    String textKey = "";

    public CraftingPage(ResourceLocation recipe) {
        this.recipe = recipe;
        this.type = ResourceLocation.fromNamespaceAndPath("patchouli", "crafting");
    }

    public CraftingPage addRecipe(ResourceLocation recipe2) {
        this.recipe2 = recipe2;
        return this;
    }

    public CraftingPage setTitle(String key) {
        this.titleKey = key;
        return this;
    }

    public CraftingPage setText(String key) {
        this.textKey = key;
        return this;
    }

    @Override
    public JsonObject toJson(String prefix, JsonObject json) {
        super.toJson(prefix, json);

        json.addProperty("recipe", recipe.toString());
        if (recipe2 != null) json.addProperty("recipe2", recipe2.toString());
        if (titleKey != null && !titleKey.isEmpty()) json.addProperty("title", key(prefix, titleKey, "title"));
        if (textKey != null && !textKey.isEmpty()) json.addProperty("text", key(prefix, textKey, "text"));
        return json;
    }
}
