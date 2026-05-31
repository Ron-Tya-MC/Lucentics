package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.ItemEntry;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.registers.LucenticsTabRegister.CategoryType;

import net.minecraft.world.item.Item;

public class LucenticsItemRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    static {
        REGISTRATE.setCreativeTab(LucenticsTabRegister.CREATIVE_MODE_TAB_INGREDIENTS);
    }

    public static final ItemEntry<Item> DUSK_BRICK = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "dusk_brick", Item::new)
            .register();

    public static void register() {
    }

}