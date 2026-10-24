package io.github.rontyamc.lucentics.datagen.providers.patchouli;

import java.util.HashMap;
import java.util.Map;

public class MacroProvider {
    private static final Map<String, String> MACROS = new HashMap<>();

    private MacroProvider() {}

    public static Map<String, String> getMacros() {
        return MACROS;
    }

    public static void register() {
        MACROS.put("#node:", "$(l:lucentics:prism/about_prisms#anchor1)");
        MACROS.put("#active:", "$(l:lucentics:prism/about_prisms#anchor2)");
        MACROS.put("#passive:", "$(l:lucentics:prism/about_prisms#anchor2)");
        MACROS.put("#exportable:", "$(l:lucentics:prism/about_prisms#anchor5)");
        MACROS.put("#foregoing:", "$(l:lucentics:getting_started/emitter#anchor3)");
        MACROS.put("#engraving:", "$(l:lucentics:devices/engraving_table#anchor2)");
    }
}
