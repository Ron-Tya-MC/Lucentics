package io.github.rontyamc.lucentics.common.datagen;

import com.google.gson.JsonObject;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.MiscFuncs;
import io.github.rontyamc.lucentics.registers.LucenticsRegistrate;

public class AddRawLang {
    static {
        JsonObject json = MiscFuncs.loadJson("/assets/lucentics/ext-lang/en_us.json");
        for (var entry : json.entrySet()) {
            Lucentics.registrate().addRawLang(entry.getKey(), entry.getValue().getAsString());
        }
    }
    public static void register() {}
}
