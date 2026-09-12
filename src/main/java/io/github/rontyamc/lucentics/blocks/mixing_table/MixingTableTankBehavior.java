package io.github.rontyamc.lucentics.blocks.mixing_table;

import io.github.rontyamc.lucentics.blocks.tank.TankBehavior;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;

public class MixingTableTankBehavior extends TankBehavior {
    public static final BehaviorType<MixingTableTankBehavior> TYPE = new BehaviorType<>("mixing_table_tank");

    public MixingTableTankBehavior(BaseBlockEntity be, int capacity) {
        super(be, capacity);
    }

    @Override
    public BehaviorType<?> getType() { return TYPE; }
}
