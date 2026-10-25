package io.github.rontyamc.lucentics.blocks.prism.prism_fluid_drain;

import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.client.particle.FlowingGlowParticleOptions;
import io.github.rontyamc.lucentics.common.beam.node.NodeActivationContext;
import io.github.rontyamc.lucentics.common.beam.node.NodeScheduleHelper;
import io.github.rontyamc.lucentics.common.beam.particle.BeamParticles;
import io.github.rontyamc.lucentics.common.beam.particle.IFlowingParticleSchedulable;
import io.github.rontyamc.lucentics.common.beam.particle.IFlowingParticleScheduleTicker;
import io.github.rontyamc.lucentics.common.beam.particle.ScheduledFlowingParticleHelper;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts.FluidExportingContext;
import io.github.rontyamc.lucentics.common.beam.transfer.IItemAcceptor;
import io.github.rontyamc.lucentics.common.beam.transfer.TransferManager;
import io.github.rontyamc.lucentics.common.util.ParticleUtil;
import io.github.rontyamc.lucentics.common.util.TransferUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;
import java.util.Optional;

public class PrismFluidDrainBehavior extends PrismBehavior implements IItemAcceptor, IFlowingParticleScheduleTicker {
    public static final PrismFluidDrainBehavior INSTANCE = new PrismFluidDrainBehavior();
    public static final int PARTICLE_INTERVAL = 6;
    public static final int PARTICLE_LINGER = 6;

    protected PrismFluidDrainBehavior() {}

    @Override
    public int getParticleLinger() {
        return PARTICLE_LINGER;
    }

    @Override
    public void activate(ServerLevel level, BlockPos prismPos, BlockPos interactPos, NodeActivationContext context) {
        FluidState fluidState = level.getFluidState(interactPos);
        if (fluidState.isEmpty() || !fluidState.isSource()) return;

        FluidStack fluidStack = new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME);
        FluidExportingContext exportingContext = TransferManager.planFluidExport(level, fluidStack, context.nodesAhead());
        if (!exportingContext.leftover().isEmpty()) return;

        boolean success = level.setBlock(interactPos, Blocks.AIR.defaultBlockState(), 3);
        if (!success) return;

        exportingContext.markProcessed();
        TransferManager.commitAndFallback(exportingContext);

        Optional<SoundEvent> placeSound = Optional.ofNullable(fluidState.getType().getFluidType().getSound(SoundActions.BUCKET_FILL));
        level.playSound(null, interactPos, placeSound.orElse(SoundEvents.BUCKET_FILL), SoundSource.BLOCKS, 1.0f, 1.0f);

        if (BeamParticles.canSpawnOnThisTick(level, prismPos)) {
            for (FluidExportingContext.FluidExportingInfo info : exportingContext.infos()) {
                ParticleUtil.spawnAndScheduleFlowing(level, interactPos, prismPos, info.targetNode(), context.color(), PARTICLE_INTERVAL);
            }
        }
    }

    @Override
    public Result acceptItems(ServerLevel level, BlockPos acceptorPos, ItemStack stack, boolean simulate) {
        Optional<IFluidHandlerItem> optionalHandler = TransferUtil.getFluidHandlerItem(stack);
        if (optionalHandler.isEmpty() || optionalHandler.get().getTanks() < 1) return Result.of(stack.copy());

        BlockPos interactPos = acceptorPos.above();
        FluidState fluidState = level.getFluidState(interactPos);
        if (fluidState.isEmpty() || !fluidState.isSource()) return Result.of(stack.copy());

        IFluidHandlerItem handler = optionalHandler.get();
        FluidStack fluidStack = new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME);

        int simulated = handler.fill(fluidStack.copyWithAmount(FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
        if (simulated < FluidType.BUCKET_VOLUME) return Result.of(stack.copy());

        if (!simulate) {
            int accepted = handler.fill(fluidStack.copyWithAmount(FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
            if (accepted < FluidType.BUCKET_VOLUME) {
                handler.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
                return Result.of(stack.copy());
            }

            boolean success = level.setBlock(interactPos, Blocks.AIR.defaultBlockState(), 3);
            if (!success) {
                handler.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
                return Result.of(stack.copy());
            }

            level.updateNeighborsAt(interactPos, Blocks.AIR);

            Optional<SoundEvent> placeSound = Optional.ofNullable(fluidState.getType().getFluidType().getSound(SoundActions.BUCKET_FILL));
            level.playSound(null, interactPos, placeSound.orElse(SoundEvents.BUCKET_FILL), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        return new Result(stack.copyWithCount(stack.getCount() - 1), List.of(handler.getContainer()));
    }

    @Override
    public void tickSchedule(ServerLevel level, BlockPos prismPos) {
        LevelChunk chunk = level.getChunkAt(prismPos);

        if (NodeScheduleHelper.tick(chunk, prismPos, IFlowingParticleSchedulable.SCHEDULE_ID) >= 0) {
            FlowingGlowParticleOptions options = ScheduledFlowingParticleHelper.get(chunk, prismPos).options();

            float d = RANDOM_SOURCE.nextFloat() * 0.3f - 0.15f;
            FlowingGlowParticleOptions newOptions =
                    new FlowingGlowParticleOptions(options.waypoints(), options.red(), options.blue(), options.green(), options.duration(),
                            RANDOM_SOURCE.nextFloat() * 0.3f + 0.15f, new Vec3(d, d, d));

            BeamParticles.spawnFlowing(level, newOptions, prismPos);
        }
    }
}
