package io.github.rontyamc.lucentics.common.datagen;

import com.google.gson.JsonObject;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.MiscFuncs;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class LangProviderJp extends LanguageProvider {
    public LangProviderJp(PackOutput output) {
        super(output, Lucentics.MOD_ID, "ja_jp");
    }

    @Override
    protected void addTranslations() {
        JsonObject json = MiscFuncs.loadJson("/assets/lucentics/ext-lang/ja_jp.json");
        for (var entry : json.entrySet()) {
            add(entry.getKey(), entry.getValue().getAsString());
        }
    }
}
