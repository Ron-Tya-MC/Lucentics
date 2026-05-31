package io.github.rontyamc.lucentics.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockEntities<T extends BlockEntity> extends EntityBlock {
    Class<T> getBlockEntityClass();

    BlockEntityType<? extends T> getBlockEntityType();

    @Override
    default BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntityType().create(pos, state);
    }
}
