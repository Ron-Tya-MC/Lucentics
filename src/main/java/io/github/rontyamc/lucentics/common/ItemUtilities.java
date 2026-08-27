package io.github.rontyamc.lucentics.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemUtilities {
    public static boolean isSameItem(ItemStack stackA, ItemStack stackB, boolean allowEmpty) {
        return allowEmpty ? stackA.is(stackB.getItem()) : !stackB.isEmpty() && stackA.is(stackB.getItem());
    }

    public static boolean canStackItems(ItemStack stackA, ItemStack stackB) {
        return isSameItem(stackA, stackB, false) && stackA.getCount() + stackB.getCount() <= stackA.getMaxStackSize();
    }

    public static void stackOrAppend(List<ItemStack> container, ItemStack addStack) {
        ItemStack newStack = addStack.copy();
        if (newStack.isEmpty()) return;

        for (ItemStack containStack : container) {
            if (!isSameItem(containStack, newStack, false)) continue;

            int remainingSpace = containStack.getMaxStackSize() - containStack.getCount();
            if (remainingSpace <= 0) continue;

            int inserted = Math.min(newStack.getCount(), remainingSpace);
            containStack.grow(inserted);
            newStack.shrink(inserted);
        }

        if (!newStack.isEmpty()) {
            container.add(newStack);
        }
    }

    public static ResourceLocation getId(Supplier<? extends ItemLike> output) {
        return BuiltInRegistries.ITEM.getKey(output.get().asItem());
    }
}
