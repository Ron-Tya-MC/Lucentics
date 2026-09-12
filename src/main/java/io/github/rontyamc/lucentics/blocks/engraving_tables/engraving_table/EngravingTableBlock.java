package io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.IBlockEntities;
import io.github.rontyamc.lucentics.common.SlotInteractions;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class EngravingTableBlock extends BaseEntityBlock implements IBlockEntities<EngravingTableBlockEntity> {
    public static final MapCodec<EngravingTableBlock> CODEC = simpleCodec(EngravingTableBlock::new);
    private static final VoxelShape BASE = Block.box(1.0, 0.0, 1.0, 15.0, 11.0, 15.0);

    private static final VoxelShape COVER_TOP = Block.box(0.0, 10.0, 0.0, 16.0, 12.0, 16.0);
    private static final VoxelShape COVER_L1_P1 = Block.box(0.0, 9.0, 0.0, 6.0, 10.0, 6.0);
    private static final VoxelShape COVER_L1_P2 = Block.box(10.0, 9.0, 0.0, 16.0, 10.0, 6.0);
    private static final VoxelShape COVER_L1_P3 = Block.box(0.0, 9.0, 10.0, 6.0, 10.0, 16.0);
    private static final VoxelShape COVER_L1_P4 = Block.box(10.0, 9.0, 10.0, 16.0, 10.0, 16.0);
    private static final VoxelShape COVER_L2_P1 = Block.box(0.0, 8.0, 0.0, 3.0, 9.0, 3.0);
    private static final VoxelShape COVER_L2_P2 = Block.box(13.0, 8.0, 0.0, 16.0, 9.0, 3.0);
    private static final VoxelShape COVER_L2_P3 = Block.box(0.0, 8.0, 13.0, 3.0, 9.0, 16.0);
    private static final VoxelShape COVER_L2_P4 = Block.box(13.0, 8.0, 13.0, 16.0, 9.0, 16.0);
    private static final VoxelShape COVER_L3_P1 = Block.box(0.0, 6.0, 0.0, 2.0, 8.0, 2.0);
    private static final VoxelShape COVER_L3_P2 = Block.box(14.0, 6.0, 0.0, 16.0, 8.0, 2.0);
    private static final VoxelShape COVER_L3_P3 = Block.box(0.0, 6.0, 14.0, 2.0, 8.0, 16.0);
    private static final VoxelShape COVER_L3_P4 = Block.box(14.0, 6.0, 14.0, 16.0, 8.0, 16.0);

    private static final VoxelShape SHAPE = Shapes.or(BASE, COVER_TOP, COVER_L1_P1, COVER_L1_P2, COVER_L1_P3, COVER_L1_P4, COVER_L2_P1, COVER_L2_P2, COVER_L2_P3, COVER_L2_P4, COVER_L3_P1, COVER_L3_P2, COVER_L3_P3, COVER_L3_P4);

    public EngravingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<EngravingTableBlockEntity> getBlockEntityClass() {
        return EngravingTableBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends EngravingTableBlockEntity> getBlockEntityType() {
        return LucenticsBlockEntityRegister.ENGRAVING_TABLE.get();
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
            if (level.getBlockEntity(pos) instanceof EngravingTableBlockEntity be) {
                be.dropContents(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof EngravingTableBlockEntity be)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        EngravingTableBehavior behavior = be.getEngravingTableBehavior();
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
                handled = true;
            }
            case SWAPPED -> {
                if (!player.isCreative()) player.setItemInHand(interactionHand, result.resultStack());
                else player.getInventory().placeItemBackInInventory(result.resultStack());
                result.fallbackStack().ifPresent(fallback -> player.getInventory().placeItemBackInInventory(fallback));
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1.5f);
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
        return new EngravingTableBlockEntity(getBlockEntityType(), pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return IBlockEntities.createTickerHelper(type, LucenticsBlockEntityRegister.ENGRAVING_TABLE.get(),
                (l, pos, s, be) -> be.tick(l, pos, s));
    }
}
