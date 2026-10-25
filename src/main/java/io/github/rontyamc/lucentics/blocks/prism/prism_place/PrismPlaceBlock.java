package io.github.rontyamc.lucentics.blocks.prism.prism_place;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.blocks.prism.PrismBlock;
import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class PrismPlaceBlock extends PrismBlock {
    public static final MapCodec<PrismPlaceBlock> CODEC = simpleCodec(PrismPlaceBlock::new);

    public PrismPlaceBlock(Properties properties) {
        super(properties, Optional.of(Colors.RED));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public PrismBehavior getPrismBehavior() {
        return PrismPlaceBehavior.INSTANCE;
    }
}