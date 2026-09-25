package io.github.rontyamc.lucentics.blocks.prism.prism_fluid_place;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.blocks.prism.PrismBlock;
import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class PrismFluidPlaceBlock extends PrismBlock {
    public static final MapCodec<PrismFluidPlaceBlock> CODEC = simpleCodec(PrismFluidPlaceBlock::new);

    public PrismFluidPlaceBlock(Properties properties) {
        super(properties, Optional.of(Colors.RED));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public PrismBehavior getPrismBehavior() {
        return PrismFluidPlaceBehavior.INSTANCE;
    }
}