package io.github.rontyamc.lucentics.blocks.prism.prism_break;

import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.client.particle.FlowingGlowParticleOptions;
import io.github.rontyamc.lucentics.common.beam.node.NodeActivationContext;
import io.github.rontyamc.lucentics.common.beam.node.NodeScheduleHelper;
import io.github.rontyamc.lucentics.common.beam.particle.BeamParticles;
import io.github.rontyamc.lucentics.common.beam.particle.IFlowingParticleSchedulable;
import io.github.rontyamc.lucentics.common.beam.particle.IFlowingParticleScheduleTicker;
import io.github.rontyamc.lucentics.common.beam.particle.ScheduledFlowingParticleHelper;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts;
import io.github.rontyamc.lucentics.common.beam.transfer.TransferManager;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import io.github.rontyamc.lucentics.common.util.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PrismBreakBehavior extends PrismBehavior implements IFlowingParticleScheduleTicker {
    public static final PrismBreakBehavior INSTANCE = new PrismBreakBehavior();
    public static final int PARTICLE_INTERVAL = 6;
    public static final int PARTICLE_LINGER = 6;

    protected PrismBreakBehavior() {}

    @Override
    public int getParticleLinger() {
        return PARTICLE_LINGER;
    }

    @Override
    public void activate(ServerLevel level, BlockPos prismPos, BlockPos interactPos, NodeActivationContext context) {
        List<ItemStack> drops = Block.getDrops(level.getBlockState(interactPos), level, interactPos, level.getBlockEntity(interactPos),
                null, ItemStack.EMPTY);

        level.destroyBlock(interactPos, false, null);

        for (ItemStack drop : drops) {
            ExportingContexts.ItemExportingContext export = TransferManager.planItemExport(level, drop, context.nodesAhead());

            export.commit();
            ItemUtil.dropItem(level, interactPos, export.fallbacks());

            if (BeamParticles.canSpawnOnThisTick(level, prismPos)) {
                for (ExportingContexts.ItemExportingContext.ItemExportingInfo info : export.infos()) {
                    ParticleUtil.spawnAndScheduleFlowing(level, prismPos, prismPos, info.targetNode(), context.color(), PARTICLE_INTERVAL);
                }
            }
        }
    }

    @Override
    public void tickSchedule(ServerLevel level, BlockPos prismPos) {
        LevelChunk chunk = level.getChunkAt(prismPos);

        long tick = NodeScheduleHelper.tick(chunk, prismPos, IFlowingParticleSchedulable.SCHEDULE_ID);

        if (tick % 2 == 1) {
            FlowingGlowParticleOptions options = ScheduledFlowingParticleHelper.get(chunk, prismPos).options();

            float d = RANDOM_SOURCE.nextFloat() * 0.2f - 0.1f;
            FlowingGlowParticleOptions newOptions =
                    new FlowingGlowParticleOptions(options.waypoints(), options.red(), options.blue(), options.green(), options.duration(),
                            RANDOM_SOURCE.nextFloat() * 0.3f + 0.05f, new Vec3(d, d, d));

            BeamParticles.spawnFlowing(level, newOptions, prismPos);
        }
    }
}
