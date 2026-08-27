package io.github.rontyamc.lucentics.blocks.emitter;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.IBlockEntities;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBlockEntity;
import io.github.rontyamc.lucentics.common.SlotInteractions;
import io.github.rontyamc.lucentics.items.LensItem;
import io.github.rontyamc.lucentics.registers.LucenticsBlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;

public class EmitterBlock extends HorizontalDirectionalBlock implements IBlockEntities<EmitterBlockEntity> {
    public static final MapCodec<EmitterBlock> CODEC = simpleCodec(EmitterBlock::new);
    private static final VoxelShape BOX = Block.box(1.0, 1.0, 1.0, 15.0, 15.0, 15.0);
    private static final VoxelShape FRAME_V1 = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 1.0);
    private static final VoxelShape FRAME_V2 = Block.box(0.0, 0.0, 15.0, 1.0, 16.0, 16.0);
    private static final VoxelShape FRAME_V3 = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape FRAME_V4 = Block.box(15.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private static final VoxelShape FRAME_H1 = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 1.0);
    private static final VoxelShape FRAME_H2 = Block.box(0.0, 0.0, 0.0, 1.0, 1.0, 16.0);
    private static final VoxelShape FRAME_H3 = Block.box(15.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    private static final VoxelShape FRAME_H4 = Block.box(0.0, 0.0, 15.0, 16.0, 1.0, 16.0);
    private static final VoxelShape FRAME_H5 = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape FRAME_H6 = Block.box(0.0, 15.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape FRAME_H7 = Block.box(15.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape FRAME_H8 = Block.box(0.0, 15.0, 15.0, 16.0, 16.0, 16.0);

    private static final VoxelShape SHAPE = Shapes.or(BOX, FRAME_V1, FRAME_V2, FRAME_V3, FRAME_V4, FRAME_H1, FRAME_H2, FRAME_H3, FRAME_H4, FRAME_H5, FRAME_H6, FRAME_H7, FRAME_H8);

    public EmitterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public Class<EmitterBlockEntity> getBlockEntityClass() {
        return EmitterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends EmitterBlockEntity> getBlockEntityType() {
        return LucenticsBlockEntityRegister.EMITTER.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EmitterBlockEntity(getBlockEntityType(), pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return IBlockEntities.createTickerHelper(type, LucenticsBlockEntityRegister.EMITTER.get(), (l, pos, s, be) -> be.tick());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof EmitterBlockEntity emitterBlockEntity) {
                emitterBlockEntity.dropContents(level, pos);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof EmitterBlockEntity be)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        EmitterBehavior behavior = be.getEmitterBehavior();
        ItemStack container = behavior.getLensContainer();
        boolean handled = false;

        SlotInteractions.SingleItemSlot lensSlot = new SlotInteractions.SingleItemSlot() {
            public ItemStack getStack() { return container; }
            public ItemStack insert(ItemStack s, boolean sim) { return behavior.insert(s, sim); }
            public ItemStack extract(int amount, boolean sim) { return behavior.extract(amount, sim); }
            public int getRemainingSpace() { return behavior.getRemainingSpace(); }
            public boolean blockMerge() { return true; }
        };

        SlotInteractions.Result result = SlotInteractions.handle(lensSlot, stack, false);
        switch (result.outcome()) {
            case INSERTED, MERGED -> {
                if (!player.isCreative()) player.setItemInHand(interactionHand, result.resultStack());
                behavior.syncColor((ServerLevel) level);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
                handled = true;
            }
            case EXTRACTED -> {
                player.getInventory().placeItemBackInInventory(result.resultStack());
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                handled = true;
            }
            case SWAPPED -> {
                if (!player.isCreative()) player.setItemInHand(interactionHand, result.resultStack());
                else player.getInventory().placeItemBackInInventory(result.resultStack());
                result.fallbackStack().ifPresent(fallback -> player.getInventory().placeItemBackInInventory(fallback));
                behavior.syncColor((ServerLevel) level);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1.5f);
                handled = true;
            }
            case NONE -> {
                if (!stack.isEmpty()) handled = true;
            }
        }

        return handled ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}