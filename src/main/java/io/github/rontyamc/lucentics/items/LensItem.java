package io.github.rontyamc.lucentics.items;

import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.world.item.Item;

public class LensItem extends Item{
    private final Colors color;

    public LensItem(Properties properties, Colors color) {
        super(properties);
        this.color = color;
    }

    public Colors getColor() {
        return color;
    }
}
