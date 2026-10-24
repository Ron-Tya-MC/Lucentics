package io.github.rontyamc.lucentics.datagen.providers.patchouli;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public interface ILuxiconPage {
    /**
     * 各変数をJsonに成型する。
     *
     * @param prefix このページが属するentryのlangキーprefix
     */
    default JsonObject toJson(String prefix) {
        JsonObject json = new JsonObject();
        toJson(prefix, json);
        return json;
    }

    /**
     *
     *
     * @param prefix このページが属するentryのlangキーprefix
     * @param json 子クラスから受け取ったJsonオブジェクト
     */
    JsonObject toJson(String prefix, JsonObject json);

    ResourceLocation getType();
}
