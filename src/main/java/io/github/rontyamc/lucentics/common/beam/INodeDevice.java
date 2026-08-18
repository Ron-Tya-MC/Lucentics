package io.github.rontyamc.lucentics.common.beam;

import net.minecraft.world.item.ItemStack;

public interface INodeDevice {
    ItemStack getContent();

    void consumeItem(int amount);
}
