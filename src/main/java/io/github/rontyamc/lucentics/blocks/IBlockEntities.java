package io.github.rontyamc.lucentics.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IBlockEntities<T extends BlockEntity> extends EntityBlock {
    Class<T> getBlockEntityClass();

    BlockEntityType<? extends T> getBlockEntityType();

    @Override
    default BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntityType().create(pos, state);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> givenType, BlockEntityType<E> targetType, BlockEntityTicker<? super E> ticker) {
        return givenType == targetType ? (BlockEntityTicker<A>) ticker : null;
    }
}
