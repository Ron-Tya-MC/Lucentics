package io.github.rontyamc.lucentics.blocks.prism;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PrismBlock extends Block implements IPrismBehavior {
    public PrismBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public PrismBehavior getPrismBehavior() {
        return null;
    }
}
