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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemStackHandler;

public class EmitterBlock extends HorizontalDirectionalBlock implements IBlockEntities<EmitterBlockEntity> {
    public static final MapCodec<EmitterBlock> CODEC = simpleCodec(EmitterBlock::new);

    public EmitterBlock(Properties properties) {
        super(properties);
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