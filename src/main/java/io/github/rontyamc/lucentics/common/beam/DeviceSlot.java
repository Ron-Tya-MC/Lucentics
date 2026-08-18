package io.github.rontyamc.lucentics.common.beam;

import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import net.minecraft.world.item.ItemStack;

public record DeviceSlot(BehaviorType<?> type, ItemStack stack, INodeDevice device) {
}
