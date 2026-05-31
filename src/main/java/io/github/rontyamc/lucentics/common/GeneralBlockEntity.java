package io.github.rontyamc.lucentics.common;

import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class GeneralBlockEntity extends BlockEntity {
    private final Map<BehaviorType<?>, BlockEntityBehavior> behaviors = new Reference2ObjectArrayMap<>();

    public GeneralBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        ArrayList<BlockEntityBehavior> list = new ArrayList<>();
        attachBehavior(list);
        list.forEach(be -> behaviors.put(be.getType(), be));
    }

    public abstract void attachBehavior(List<BlockEntityBehavior> behaviors);

    public <T extends BlockEntityBehavior> T getBehavior(BehaviorType<T> type) {
        return (T) behaviors.get(type);
    }
}
