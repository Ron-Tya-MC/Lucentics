package io.github.rontyamc.lucentics.blocks.prism.prism_collecting;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.blocks.prism.prism_io.PrismIOBlock;
import io.github.rontyamc.lucentics.common.beam.particle.IFlowingParticleScheduleTicker;
import net.minecraft.world.level.block.Block;

public class PrismCollectingBlock extends PrismIOBlock {
    public static final MapCodec<PrismCollectingBlock> CODEC = simpleCodec(PrismCollectingBlock::new);

    public PrismCollectingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public PrismBehavior getPrismBehavior() {
        return PrismCollectingBehavior.INSTANCE;
    }

    @Override
    public IFlowingParticleScheduleTicker getTicker() {
        return PrismCollectingBehavior.INSTANCE;
    }
}