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
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class EngravingTableBlock extends BaseEntityBlock implements IBlockEntities<EngravingTableBlockEntity> {
    public static final MapCodec<EngravingTableBlock> CODEC = simpleCodec(EngravingTableBlock::new);
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

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
        ItemStack container = behavior.getContainer();
        boolean handled = false;

        SlotInteractions.SingleItemSlot slot = new SlotInteractions.SingleItemSlot() {
            public ItemStack getStack() { return container; }
            public ItemStack insert(ItemStack s, boolean sim) { return behavior.insert(s, sim); }
            public ItemStack extract(int amount, boolean sim) { return behavior.extract(amount, sim); }
            public int getRemainingSpace() { return behavior.getRemainingSpace(); }
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
