package io.github.rontyamc.lucentics.blocks.prism.prism_attack;

import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.common.beam.node.NodeActivationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class PrismAttackBehavior extends PrismBehavior {
    public static final PrismAttackBehavior INSTANCE = new PrismAttackBehavior();

    protected PrismAttackBehavior() {}

    @Override
    public void activate(ServerLevel level, BlockPos prismPos, BlockPos interactPos, NodeActivationContext context) {
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(interactPos));
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().generic(), 1.0f);
        }
    }
}
