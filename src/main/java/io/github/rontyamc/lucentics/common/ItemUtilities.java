package io.github.rontyamc.lucentics.common;

import net.minecraft.world.item.ItemStack;

public class ItemUtilities {
    public static boolean isSameItem(ItemStack stackA, ItemStack stackB, boolean allowEmpty) {
        return allowEmpty ? stackA.is(stackB.getItem()) : !stackB.isEmpty() && stackA.is(stackB.getItem());
    }

    public static boolean canStackItems(ItemStack stackA, ItemStack stackB) {
        return isSameItem(stackA, stackB, false) && stackA.getCount() + stackB.getCount() <= stackA.getMaxStackSize();
    }
}
