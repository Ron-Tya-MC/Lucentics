package io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual;

import io.github.rontyamc.lucentics.blocks.pedestals.PedestalBehavior;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;

public class PedestalRitualBehavior extends PedestalBehavior {
    public static final BehaviorType<PedestalRitualBehavior> TYPE = new BehaviorType<>("pedestal_ritual");

    public PedestalRitualBehavior(BaseBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }
}
