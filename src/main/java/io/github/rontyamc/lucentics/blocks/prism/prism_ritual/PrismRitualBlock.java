package io.github.rontyamc.lucentics.blocks.prism.prism_ritual;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.blocks.prism.PrismBlock;
import net.minecraft.world.level.block.Block;

public class PrismRitualBlock extends PrismBlock {
    public static final MapCodec<PrismRitualBlock> CODEC = simpleCodec(PrismRitualBlock::new);

    public PrismRitualBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public PrismBehavior getPrismBehavior() {
        return PrismRitualBehavior.INSTANCE;
    }
}
