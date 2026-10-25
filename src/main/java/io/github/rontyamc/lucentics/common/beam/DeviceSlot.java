package io.github.rontyamc.lucentics.common.beam;

import io.github.rontyamc.lucentics.common.ThingStack;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;

public record DeviceSlot(BehaviorType<?> type, ThingStack stack, INodeDevice device) {
}
