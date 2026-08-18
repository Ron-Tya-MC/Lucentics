package io.github.rontyamc.lucentics.common;

import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class BaseBlockEntity extends BlockEntity {
    private final Map<BehaviorType<?>, BlockEntityBehavior> behaviors = new Reference2ObjectArrayMap<>();

    public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        ArrayList<BlockEntityBehavior> list = new ArrayList<>();
        attachBehavior(list);
        list.forEach(beb -> behaviors.put(beb.getType(), beb));
    }

    public abstract void attachBehavior(List<BlockEntityBehavior> behaviors);

    public <T extends BlockEntityBehavior> T getBehavior(BehaviorType<T> type) {
        return (T) behaviors.get(type);
    }

    public Vec3 getCenter(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public void updated() {
        setChanged();
        notifyChanged();
    }

    public void notifyChanged() {
        if (level instanceof ServerLevel server)
            server.getChunkSource().blockChanged(getBlockPos());
    }

    public Optional<BlockEntityBehavior> findBehavior(Predicate<BlockEntityBehavior> predicate) {
        return behaviors.values().stream().filter(predicate).findFirst();
    }
}
