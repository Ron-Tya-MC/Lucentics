package io.github.rontyamc.lucentics.blocks.prism;

import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class PrismBlock extends Block implements IPrismBehavior {
    /**
     * ここで指定した色の光でのみ動作する。
     * {@link Optional#empty()}は光の色に制限なしという意味を持つ。
      */
    private Optional<Colors> requiredColor;

    public PrismBlock(Properties properties, Optional<Colors> requiredColor) {
        super(properties);
        this.requiredColor = requiredColor;
    }

    public PrismBlock(Properties properties) {
        this(properties, Optional.empty());
    }

    public Optional<Colors> getRequiredColor() {
        return requiredColor;
    }

    public void setRequiredColor(Colors requiredColor) {
        this.requiredColor = Optional.of(requiredColor);
    }

    public void clearRequiredColor() {
        this.requiredColor = Optional.empty();
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
