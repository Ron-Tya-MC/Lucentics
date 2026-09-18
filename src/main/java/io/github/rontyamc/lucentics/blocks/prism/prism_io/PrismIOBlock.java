package io.github.rontyamc.lucentics.blocks.prism.prism_io;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.blocks.prism.prism_importing.PrismImportingBlock;
import io.github.rontyamc.lucentics.common.beam.particle.IFlowingParticleScheduleTicker;
import net.minecraft.world.level.block.Block;

public class PrismIOBlock extends PrismImportingBlock {
    public static final MapCodec<PrismIOBlock> CODEC = simpleCodec(PrismIOBlock::new);

    public PrismIOBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public PrismBehavior getPrismBehavior() {
        return PrismIOBehavior.INSTANCE;
    }

    @Override
    public IFlowingParticleScheduleTicker getTicker() {
        return PrismIOBehavior.INSTANCE;
    }
}