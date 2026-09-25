package io.github.rontyamc.lucentics.blocks.prism.prism_attack;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.blocks.prism.PrismBlock;
import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class PrismAttackBlock extends PrismBlock {
    public static final MapCodec<PrismAttackBlock> CODEC = simpleCodec(PrismAttackBlock::new);

    public PrismAttackBlock(Properties properties) {
        super(properties, Optional.of(Colors.RED));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public PrismBehavior getPrismBehavior() {
        return PrismAttackBehavior.INSTANCE;
    }
}