package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.ItemEntry;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.items.LensItem;
import io.github.rontyamc.lucentics.registers.LucenticsTabRegister.CategoryType;

import net.minecraft.world.item.Item;

import java.util.EnumMap;
import java.util.Map;

public class LucenticsItemRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    static {
        REGISTRATE.setCreativeTab(LucenticsTabRegister.CREATIVE_MODE_TAB_INGREDIENTS);
    }

    public static final ItemEntry<Item> DUSK_BRICK = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "dusk_brick", Item::new)
            .register();

    public static final Map<Colors, ItemEntry<LensItem>> LENSES = registerLenses();

    private static Map<Colors, ItemEntry<LensItem>> registerLenses() {
        Map<Colors, ItemEntry<LensItem>> map = new EnumMap<>(Colors.class);
        for (Colors color : Colors.values()) {
            if (color == Colors.SUNLIGHT) continue; // SUNLIGHT色のレンズなんてないよ
            String name = color.getName() + "_lens";
            ItemEntry<LensItem> entry = REGISTRATE
                    .lucenticsItemBuilder(CategoryType.INGREDIENTS, name, p -> new LensItem(p.stacksTo(64), color))
                    .register();
            map.put(color, entry);
        }
        return map;
    }

    public static void register() {
    }
}