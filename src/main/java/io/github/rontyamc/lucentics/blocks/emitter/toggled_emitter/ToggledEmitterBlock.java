package io.github.rontyamc.lucentics.blocks.emitter.toggled_emitter;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.IBlockEntities;
import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlock;
import io.github.rontyamc.lucentics.registers.LucenticsBlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ToggledEmitterBlock extends EmitterBlock {
    public static final MapCodec<ToggledEmitterBlock> CODEC = simpleCodec(ToggledEmitterBlock::new);

    public ToggledEmitterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntityType<? extends ToggledEmitterBlockEntity> getBlockEntityType() {
        return LucenticsBlockEntityRegister.TOGGLED_EMITTER.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ToggledEmitterBlockEntity(getBlockEntityType(), pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return IBlockEntities.createTickerHelper(type, LucenticsBlockEntityRegister.TOGGLED_EMITTER.get(), (l, pos, s, be) -> be.tick());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof ToggledEmitterBlockEntity emitterBlockEntity) {
                emitterBlockEntity.dropContents(level, pos);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}