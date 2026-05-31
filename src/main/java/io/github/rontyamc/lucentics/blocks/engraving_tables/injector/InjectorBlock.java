package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.IBlockEntities;
import io.github.rontyamc.lucentics.registers.LucenticsBlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;

public class InjectorBlock extends BaseEntityBlock implements IBlockEntities {
    public static final MapCodec<InjectorBlock> CODEC = simpleCodec(InjectorBlock::new);
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

    @Override
    public Class<InjectorBlockEntity> getBlockEntityClass() {
        return InjectorBlockEntity.class;
    }
    @Override
    public BlockEntityType<? extends InjectorBlockEntity> getBlockEntityType() {
        return LucenticsBlockEntityRegister.INJECTOR.get();
    }

    public InjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState shape) {
        return RenderShape.MODEL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof InjectorBlockEntity injectorBlockEntity) {
                injectorBlockEntity.dropContents();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        if(level.getBlockEntity(pos) instanceof InjectorBlockEntity injectorBlockEntity) {
            ItemStackHandler inv = injectorBlockEntity.inventory;

            if(inv.getStackInSlot(0).isEmpty()) {
                if(!stack.isEmpty()) {
                    inv.insertItem(0, stack.copy(), false);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
                }
            }
            else {
                ItemStack contentStack = inv.extractItem(0, 1, false);
                if(!stack.isEmpty()) {
                    injectorBlockEntity.clearContents();

                    inv.insertItem(0, stack.copy(), false);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }

                    player.getInventory().placeItemBackInInventory(contentStack);
                    level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
                }
                else {
                    player.getInventory().placeItemBackInInventory(contentStack);
                    injectorBlockEntity.clearContents();
                    level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                }
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InjectorBlockEntity(getBlockEntityType(), pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, LucenticsBlockEntityRegister.INJECTOR.get(), (tLevel, pos, state1, blockEntity) -> blockEntity.tick(tLevel, pos, state1));
    }
}