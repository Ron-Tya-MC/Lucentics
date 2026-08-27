package io.github.rontyamc.lucentics.common.behavior;

import io.github.rontyamc.lucentics.blocks.prism.IPrismBehavior ;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.beam.BeamNode;
import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public abstract class EmitBehavior extends BlockEntityBehavior {
    public static final BehaviorType<EmitBehavior> TYPE = new BehaviorType<>("emit");
    private static final int MAX_DISTANCE = 8;

    protected int beamLength = 0;

    private List<BeamNode> trail = List.of();
    private BeamNode endpoint = null;
    protected Colors color = Colors.SUNLIGHT;

    private boolean stopBeam = true;

    public EmitBehavior(BaseBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    public int getBeamLength() { return beamLength; }
    public Colors getColor() { return color; }

    public void setColor(Colors color) { this.color = color; }

    public void setStopBeam(boolean bool) { stopBeam = bool; }

    public BeamNode getEndpoint() { return endpoint; }

    @Override
    public void tick() {
        super.tick();

        Level level = getWorld();
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (stopBeam) {
            if (!trail.isEmpty() || endpoint != null || beamLength != 0) {
                clearBeam(serverLevel);
            }
            stopBeam = false;
            return;
        }

        Direction facing = getFacing();
        int newLength = rayCast(serverLevel, getPos(), facing);
        if (newLength != beamLength) {
            triggerFullScan(serverLevel, facing);
        }

        for (BeamNode node : trail) {
            BlockState state = serverLevel.getBlockState(node.pos());
            if (state.getBlock() instanceof IPrismBehavior source) {
                source.getPrismBehavior().activate(serverLevel, node.pos(), node.pos().above());
            }
        }

        if (endpoint != null) {
            BlockEntity be = level.getBlockEntity(endpoint.pos());
            if (be instanceof BaseBlockEntity base) {
                base.findBehavior(b -> b instanceof ReceiveBehavior).ifPresent(behavior -> {
                    ReceiveBehavior receive = (ReceiveBehavior) behavior;
                    receive.onBeamReceived(trail, color);
                });
            }
        }
    }

    protected void scan(ServerLevel level, BlockPos origin, Direction direction, List<BeamNode> precedingTrail, Colors color) {
        List<BeamNode> newTrail = new ArrayList<>(precedingTrail);
        BeamNode newEndpoint = null;
        int reachedDistance = MAX_DISTANCE;

        for (int distance = 1; distance <= MAX_DISTANCE; distance++) {
            BlockPos checkPos = origin.relative(direction, distance);
            BlockState state = level.getBlockState(checkPos);

            if (state.getBlock() instanceof IPrismBehavior) {
                ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                newTrail.add(new BeamNode(blockId, checkPos.immutable(), level.dimension()));
                continue;
            }

            if (!state.canOcclude()) {
                continue;
            }

            BlockEntity be = level.getBlockEntity(checkPos);
            if (be instanceof BaseBlockEntity base) {
                if (base.findBehavior(b -> b instanceof ReceiveBehavior).isPresent()) {
                    ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                    newEndpoint = new BeamNode(blockId, checkPos.immutable(), level.dimension());
                }
            }

            reachedDistance = distance - 1;
            break;
        }

        if (newEndpoint == null) {
            BlockPos checkPos = origin.relative(direction, MAX_DISTANCE + 1);
            BlockState state = level.getBlockState(checkPos);
            BlockEntity be = level.getBlockEntity(checkPos);
            if (be instanceof BaseBlockEntity base) {
                if (base.findBehavior(b -> b instanceof ReceiveBehavior).isPresent()) {
                    ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                    newEndpoint = new BeamNode(blockId, checkPos.immutable(), level.dimension());
                }
            }
        }

        this.trail = newTrail;
        this.endpoint = newEndpoint;
        this.color = color;
        this.beamLength = reachedDistance;
    }

    protected int rayCast(ServerLevel level, BlockPos origin, Direction direction) {
        for (int distance = 1; distance <= MAX_DISTANCE; distance++) {
            BlockPos checkPos = origin.relative(direction, distance);
            BlockState state = level.getBlockState(checkPos);

            if (state.getBlock() instanceof IPrismBehavior) continue;

            if (!state.canOcclude()) continue;

            return distance - 1;
        }
        return MAX_DISTANCE;
    }

    private void clearBeam(ServerLevel level) {
        this.trail = List.of();
        this.endpoint = null;
        this.beamLength = 0;
        level.sendBlockUpdated(getPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
    }

    protected abstract Direction getFacing();
    protected abstract void triggerFullScan(ServerLevel level, Direction facing);
}
