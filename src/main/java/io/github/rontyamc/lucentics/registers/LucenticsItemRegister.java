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
    public static final ItemEntry<Item> RED_DUSK_BRICK = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "red_dusk_brick", Item::new)
            .register();
    public static final ItemEntry<Item> BLUE_DUSK_BRICK = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "blue_dusk_brick", Item::new)
            .register();
    public static final ItemEntry<Item> GREEN_DUSK_BRICK = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "green_dusk_brick", Item::new)
            .register();

    public static final ItemEntry<Item> LIGHT_COPPER = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "light_copper", Item::new)
            .register();
    public static final ItemEntry<Item> YELLOW_LIGHT_COPPER = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "yellow_light_copper", Item::new)
            .register();
    public static final ItemEntry<Item> MAGENTA_LIGHT_COPPER = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "magenta_light_copper", Item::new)
            .register();
    public static final ItemEntry<Item> LIGHT_BLUE_LIGHT_COPPER = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "light_blue_light_copper", Item::new)
            .register();

    public static final ItemEntry<Item> LENS_FRAME = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "lens_frame", Item::new)
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