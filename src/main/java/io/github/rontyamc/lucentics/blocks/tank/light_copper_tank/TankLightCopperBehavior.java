package io.github.rontyamc.lucentics.blocks.tank.light_copper_tank;

import io.github.rontyamc.lucentics.blocks.pedestals.PedestalBehavior;
import io.github.rontyamc.lucentics.blocks.tank.TankBehavior;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import net.neoforged.neoforge.fluids.FluidType;

public class TankLightCopperBehavior extends TankBehavior {
    public static final BehaviorType<TankLightCopperBehavior> TYPE = new BehaviorType<>("tank_light_copper");

    public TankLightCopperBehavior(BaseBlockEntity be) {
        super(be, 16 * FluidType.BUCKET_VOLUME);
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }
}
