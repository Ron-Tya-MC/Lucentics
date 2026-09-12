package io.github.rontyamc.lucentics.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;

public final class ItemUtil {
    public static boolean isSameItem(ItemStack stackA, ItemStack stackB, boolean allowEmpty) {
        return allowEmpty ? stackA.is(stackB.getItem()) : !stackB.isEmpty() && stackA.is(stackB.getItem());
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

    public static ItemStack hurtAndUpdate(int damage, ItemStack stack) {
        int currentDamage = stack.getDamageValue();
        stack.setDamageValue(currentDamage + damage);
        return currentDamage + damage < stack.getMaxDamage() ? stack : ItemStack.EMPTY;
    }

    public static void dropItem(Level level, BlockPos pos, ItemStack stack) {
        Vec3 vec = Vec3.atCenterOf(pos);
        Containers.dropItemStack(level, vec.x, vec.y, vec.z, stack);
    }

    public static void dropItem(Level level, BlockPos pos, List<ItemStack> stacks) {
        if (level == null) return;
        if (level.isClientSide) return;
        Vec3 vec = Vec3.atCenterOf(pos);

        if (!stacks.isEmpty()) {
            for (ItemStack stack : stacks) {
                Containers.dropItemStack(level, vec.x, vec.y, vec.z, stack);
            }
        }
    }
}
