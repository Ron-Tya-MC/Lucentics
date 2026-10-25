package io.github.rontyamc.lucentics.client.patchouli;

import com.google.gson.JsonObject;
import io.github.rontyamc.lucentics.datagen.providers.patchouli.ILuxiconPage;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public abstract class BasePage<T extends BasePage<T>> implements ILuxiconPage {
    protected ResourceLocation type;
    @Nullable
    protected ResourceLocation advancement = null;
    protected String flag = "";
    protected String anchor = "";

    protected static String key(String prefix, String key, String field) {
        return prefix + "." + field + "." + key;
    }

    public ResourceLocation getType() {
        return type;
    }

    public T setAdvancement(ResourceLocation advancement) {
        this.advancement = advancement;
        return (T) this;
    }

    public T setFlag(String flag) {
        this.flag = flag;
        return (T) this;
    }

    public T setAnchor(String anchor) {
        this.anchor = anchor;
        return (T) this;
    }

    @Override
    public JsonObject toJson(String prefix, JsonObject json) {
        json.addProperty("type", type.toString());
        if (advancement != null) json.addProperty("advancement", advancement.toString());
        if (flag != null && !flag.isEmpty()) json.addProperty("flag", flag);
        if (anchor != null && !anchor.isEmpty()) json.addProperty("anchor", anchor);
        return json;
    }
}
