package io.github.rontyamc.lucentics.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ItemUtil {
    public static boolean isSameItem(ItemStack stackA, ItemStack stackB, boolean allowEmpty) {
        return allowEmpty ? stackA.is(stackB.getItem()) : !stackB.isEmpty() && stackA.is(stackB.getItem());
    }

    public static List<ItemStack> stackOrAppend(List<ItemStack> container, ItemStack addStack) {
        ItemStack newStack = addStack.copy();
        List<ItemStack> newContainer = deepCopy(container);

        if (newStack.isEmpty()) return newContainer;

        for (ItemStack containStack : newContainer) {
            if (!isSameItem(containStack, newStack, false)) continue;

            int remainingSpace = containStack.getMaxStackSize() - containStack.getCount();
            if (remainingSpace <= 0) continue;

            int inserted = Math.min(newStack.getCount(), remainingSpace);
            containStack.grow(inserted);
            newStack.shrink(inserted);
        }

        if (!newStack.isEmpty()) {
            newContainer.add(newStack);
        }

        return newContainer;
    }

    public static List<ItemStack> deepCopy(List<ItemStack> origin) {
        return origin.stream().map(item -> item != null ? item.copy() : null).collect(Collectors.toCollection(ArrayList::new));
    }

    public static ResourceLocation getId(Supplier<? extends ItemLike> item) {
        return BuiltInRegistries.ITEM.getKey(item.get().asItem());
    }

    public static Item byId(ResourceLocation id) {
        return BuiltInRegistries.ITEM.stream()
                .filter(item -> getId(() -> item).equals(id))
                .findFirst().orElse(Items.AIR);
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

    public static boolean isItemBlock(Item item) {
        return !Block.byItem(item).defaultBlockState().isAir();
    }

    public static boolean isIdInTag(ResourceLocation id, TagKey<Item> tagKey, Level level) {
        return level.registryAccess().lookup(Registries.ITEM)
                .flatMap(registry -> registry.get(ResourceKey.create(Registries.ITEM, id)))
                .map(holder -> holder.is(tagKey))
                .orElse(false);
    }

    public static List<ItemStack> itemsInItemTag(TagKey<Item> tagKey) {
        Ingredient tagged = Ingredient.of(tagKey);

        return List.of(tagged.getItems());
    }

    public static List<Item> itemsInBlockTag(TagKey<Block> tagKey, Level level) {
        return level.registryAccess().lookup(Registries.BLOCK)
                .flatMap(registry -> registry.get(tagKey))
                .map(holderSet -> holderSet.stream()
                        .map(holder -> holder.value().asItem())
                        .filter(item -> !item.equals(Items.AIR))
                        .toList())
                .orElse(List.of());
    }

    public record ItemMergeResult(ItemStack merged, ItemStack leftover) {
        public static ItemMergeResult of(ItemStack merged, ItemStack leftover) {
            return new ItemMergeResult(merged, leftover);
        }
    }

    public static ItemMergeResult merge(ItemStack base, ItemStack add) {
        if (add.isEmpty()) return ItemMergeResult.of(base.copy(), ItemStack.EMPTY);
        if (!isSameItem(base, add, false)) return ItemMergeResult.of(base.copy(), add.copy());

        int total = base.getCount() + add.getCount();
        return total <= base.getMaxStackSize()
                ? ItemMergeResult.of(base.copyWithCount(total), ItemStack.EMPTY)
                : ItemMergeResult.of(base.copyWithCount(base.getMaxStackSize()), add.copyWithCount(total - base.getMaxStackSize()));
    }
}
