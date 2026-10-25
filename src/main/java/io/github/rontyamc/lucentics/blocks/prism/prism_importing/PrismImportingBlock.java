package io.github.rontyamc.lucentics.blocks.prism.prism_importing;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.blocks.prism.PrismBlock;
import io.github.rontyamc.lucentics.client.particle.FlowingGlowParticleOptions;
import io.github.rontyamc.lucentics.common.beam.node.NodeScheduleHelper;
import io.github.rontyamc.lucentics.common.beam.particle.IFlowingParticleSchedulable;
import io.github.rontyamc.lucentics.common.beam.particle.IFlowingParticleScheduleTicker;
import io.github.rontyamc.lucentics.common.beam.particle.ScheduledFlowingParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;

public class PrismImportingBlock extends PrismBlock implements IFlowingParticleSchedulable {
    public static final MapCodec<PrismImportingBlock> CODEC = simpleCodec(PrismImportingBlock::new);

    public PrismImportingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public PrismBehavior getPrismBehavior() {
        return PrismImportingBehavior.INSTANCE;
    }

    @Override
    public void registerContext(ServerLevel level, BlockPos pos, FlowingGlowParticleOptions options) {
        LevelChunk chunk = level.getChunkAt(pos);
        ScheduledFlowingParticleHelper.set(chunk, pos, options);
    }

    @Override
    public void registerSchedule(ServerLevel level, BlockPos pos, ResourceLocation id, int timer) {
        LevelChunk chunk = level.getChunkAt(pos);
        NodeScheduleHelper.append(chunk, pos, id, timer, false);
    }

    @Override
    public IFlowingParticleScheduleTicker getTicker() {
        return PrismImportingBehavior.INSTANCE;
    }
}