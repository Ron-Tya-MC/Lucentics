package io.github.rontyamc.lucentics.common.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.MiscFuncs;
import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.ArrayList;
import java.util.Map;

public class LangProviderJp extends LanguageProvider {
    public LangProviderJp(PackOutput output) {
        super(output, Lucentics.MOD_ID, "ja_jp");
    }

    // $R記法 見本
    //     "$R:item.lucentics;16_colors/_lens" : "%Rのレンズ"

    @Override
    protected void addTranslations() {
        JsonObject json = MiscFuncs.loadJson("/assets/lucentics/ext-lang/ja_jp.json");
        for (var entry : json.entrySet()) {
            String key = entry.getKey();

            Lucentics.LOGGER.debug("try: {}", key);

            if (key.startsWith("$R:")) {
                String prefix = key.substring(3, key.lastIndexOf(';'));
                String suffix = key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : "";
                String direction = key.contains("/")
                        ? key.substring(key.lastIndexOf(';') + 1, key.lastIndexOf('/'))
                        : key.substring(key.lastIndexOf(';') + 1);

                switch (direction) {
                    case "16_colors" -> addByColors(Colors.get16Colors(), prefix, suffix, entry, json);
                    case "tier1_colors" -> addByColors(Colors.byTier(1), prefix, suffix, entry, json);
                    case "tier2_colors" -> addByColors(Colors.byTier(2), prefix, suffix, entry, json);
                    case "tier3_colors" -> addByColors(Colors.byTier(3), prefix, suffix, entry, json);
                    case "tier4_colors" -> addByColors(Colors.byTier(4), prefix, suffix, entry, json);
                    case "tier5_colors" -> addByColors(Colors.byTier(5), prefix, suffix, entry, json);
                    case "tier6_colors" -> addByColors(Colors.byTier(6), prefix, suffix, entry, json);
                    case "tier7_colors" -> addByColors(Colors.byTier(7), prefix, suffix, entry, json);
                    default -> throw new IllegalStateException("Replace direction \"" + direction + "\" is undefined");
                }
            } else {
                Lucentics.LOGGER.debug("added: {}", key);
                add(key, entry.getValue().getAsString());
            }
        }
    }

    private void addByColors(ArrayList<Colors> colors, String prefix, String suffix, Map.Entry<String, com.google.gson.JsonElement> entry, JsonObject json) {
        String refKey;
        String actualKey;
        String actualValue;
        JsonElement refElement;

        for (var color : colors) {
            refKey = "term.lucentics.colors." + color.getName();
            actualKey = prefix + "." + color.getName() + suffix;
            refElement = json.get(refKey);
            if (refElement == null) {
                throw new IllegalStateException("Missing reference key: " + refKey + " (required by " + actualKey + ")");
            }
            actualValue = entry.getValue().getAsString().replace("%R", refElement.getAsString());

            Lucentics.LOGGER.debug("added: {}", actualKey);
            add(actualKey, actualValue);
        }
    }
}
