package io.github.rontyamc.lucentics.blocks.emitter.advanced_emitter;

import io.github.rontyamc.lucentics.blocks.emitter.EmitterBehavior;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;

public class AdvancedEmitterBehavior extends EmitterBehavior implements Clearable {
    public static final BehaviorType<AdvancedEmitterBehavior> TYPE = new BehaviorType<>("advanced_emitter", EmitterBehavior.TYPE);

    public static final int MAX_DISTANCE = 16;

    public AdvancedEmitterBehavior(BaseBlockEntity be) {
        super(be);

        setMaxDistance(MAX_DISTANCE);
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    @Override
    protected Direction getFacing() {
        return blockEntity.getBlockState().getValue(AdvancedEmitterBlock.FACING);
    }

    @Override
    protected void triggerFullScan(ServerLevel level, Direction facing) {
        super.triggerFullScan(level, facing);
    }

    protected void syncColor(ServerLevel level) {
        super.syncColor(level);
    }

    @Override
    protected void lazyTick() {
        super.lazyTick();
    }


}
