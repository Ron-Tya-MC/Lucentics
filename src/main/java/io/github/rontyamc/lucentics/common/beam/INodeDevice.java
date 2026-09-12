package io.github.rontyamc.lucentics.common.beam;

import io.github.rontyamc.lucentics.common.ThingStack;

public interface INodeDevice {
    ThingStack getStack();

    void consume(int amount);
    void damageItem(int damage);
    void catalyst();
}
