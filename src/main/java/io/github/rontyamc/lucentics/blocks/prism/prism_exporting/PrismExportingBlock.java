package io.github.rontyamc.lucentics.blocks.prism.prism_exporting;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.blocks.prism.PrismBlock;
import net.minecraft.world.level.block.Block;

public class PrismExportingBlock extends PrismBlock {
    public static final MapCodec<PrismExportingBlock> CODEC = simpleCodec(PrismExportingBlock::new);

    public PrismExportingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public PrismBehavior getPrismBehavior() {
        return PrismExportingBehavior.INSTANCE;
    }
}
