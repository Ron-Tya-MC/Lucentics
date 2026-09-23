package io.github.rontyamc.lucentics.blocks.assembling_table;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.IBlockEntities;
import io.github.rontyamc.lucentics.common.SlotInteractions;
import io.github.rontyamc.lucentics.registers.LucenticsBlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class AssemblingTableBlock extends BaseEntityBlock implements IBlockEntities<AssemblingTableBlockEntity> {
    public static final MapCodec<AssemblingTableBlock> CODEC = simpleCodec(AssemblingTableBlock::new);
    private static final VoxelShape BASE = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);

    private static final VoxelShape POLE = Block.box(7.0, 4.0, 7.0, 9.0, 9.0, 9.0);

    private static final VoxelShape TOP = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape FRAME_P1 = Block.box(2.0, 4.0, 2.0, 3.0, 14.0, 3.0);
    private static final VoxelShape FRAME_P2 = Block.box(2.0, 4.0, 13.0, 3.0, 14.0, 14.0);
    private static final VoxelShape FRAME_P3 = Block.box(13.0, 4.0, 2.0, 14.0, 14.0, 3.0);
    private static final VoxelShape FRAME_P4 = Block.box(13.0, 4.0, 13.0, 14.0, 14.0, 14.0);

    private static final VoxelShape TABLE = Block.box(3.0, 4.0, 3.0, 13.0, 5.0, 13.0);

    private static final VoxelShape SHAPE = Shapes.or(BASE, POLE, TOP, FRAME_P1, FRAME_P2, FRAME_P3, FRAME_P4, TABLE);

    public AssemblingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<AssemblingTableBlockEntity> getBlockEntityClass() {
        return AssemblingTableBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AssemblingTableBlockEntity> getBlockEntityType() {
        return LucenticsBlockEntityRegister.ASSEMBLING_TABLE.get();
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
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof AssemblingTableBlockEntity be) {
                be.dropContents(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }


    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof AssemblingTableBlockEntity be)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!(level instanceof ServerLevel serverLevel)) return ItemInteractionResult.SUCCESS;

        AssemblingTableBehavior behavior = be.getAssemblingTableBehavior();
        ItemStack container = behavior.getContainer().asItemOrEmpty();
        boolean handled = false;

        SlotInteractions.SingleItemSlot slot = new SlotInteractions.SingleItemSlot() {
            public ItemStack getStack() { return container; }
            public ItemStack insert(ItemStack s, boolean sim) { return behavior.insert(s, sim); }
            public ItemStack extract(int amount, boolean sim) { return behavior.extract(amount, sim); }
            public int getRemainingSpace() { return behavior.getRemainingSpace(); }
            public boolean blockMerge() { return behavior.getBlockMerge(); }
        };

        SlotInteractions.Result result = SlotInteractions.handle(slot, stack, false);
        switch (result.outcome()) {
            case INSERTED, MERGED -> {
                if (!player.isCreative()) player.setItemInHand(interactionHand, result.resultStack());
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
                handled = true;
            }
            case EXTRACTED -> {
                player.getInventory().placeItemBackInInventory(result.resultStack());
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                behavior.resetProcessingContinue(serverLevel);
                handled = true;
            }
            case SWAPPED -> {
                if (!player.isCreative()) player.setItemInHand(interactionHand, result.resultStack());
                else player.getInventory().placeItemBackInInventory(result.resultStack());
                result.fallbackStack().ifPresent(fallback -> player.getInventory().placeItemBackInInventory(fallback));
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1.5f);
                behavior.resetProcessingContinue(serverLevel);
                handled = true;
            }
            case NONE -> {}
        }

        if (!behavior.getBuffer().isEmpty()) {
            List<ItemStack> collected = behavior.collectBuffer();
            for (ItemStack collectedStack : collected) {
                player.getInventory().placeItemBackInInventory(collectedStack);
            }
            handled = true;
        }

        return handled ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AssemblingTableBlockEntity(getBlockEntityType(), pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return IBlockEntities.createTickerHelper(type, LucenticsBlockEntityRegister.ASSEMBLING_TABLE.get(),
                (l, pos, s, be) -> be.tick(l, pos, s));
    }
}
