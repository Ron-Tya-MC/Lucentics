package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import io.github.rontyamc.lucentics.Lucentics;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class LucenticsItemRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    public static final ItemEntry<Item> DUSK_BRICK = setCategory("ingredients", "dusk_brick", Item::new);

    public static final ItemEntry<Item> DUSK_BRICK_2 = setCategory("blocks", "dusk_brick_2", Item::new);

    public static void register() {
        REGISTRATE.setCreativeTab(LucenticsTabRegister.CREATIVE_MODE_TAB_INGREDIENTS);
    }

    public static <T extends Item> ItemEntry<Item> setCategory(String category, String name, NonNullFunction<Item.Properties, Item> factory) {
        LucenticsTabRegister.ITEM_CATEGORY.put(Lucentics.MOD_ID + ":" + name, category);

        return REGISTRATE.item(name, factory).register();
    }
}