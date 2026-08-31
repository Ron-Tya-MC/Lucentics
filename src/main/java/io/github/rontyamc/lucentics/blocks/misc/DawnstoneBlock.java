package io.github.rontyamc.lucentics.blocks.misc;

import io.github.rontyamc.lucentics.common.MiscFuncs;
import io.github.rontyamc.lucentics.items.HammerItem;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class DawnstoneBlock extends Block {
    public static final IntegerProperty CRACKS = IntegerProperty.create("crack", 0, 9);
    public static final Integer MAX_CRACKS = 10;

    public DawnstoneBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CRACKS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CRACKS);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            level.destroyBlockProgress(pos.hashCode(), pos, -1);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(stack.getItem() instanceof HammerItem hammer)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        int currentCracks = state.getValue(CRACKS) + hammer.getCracksPerClick();

        if (currentCracks < MAX_CRACKS) {
            level.destroyBlockProgress(pos.hashCode(), pos, currentCracks);
        } else {
            level.destroyBlockProgress(pos.hashCode(), pos, -1);
        }

        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        if (currentCracks >= MAX_CRACKS) {
            level.destroyBlock(pos, false, player);

            Vec3 vec = MiscFuncs.getCenter(pos);
            ItemStack dust = new ItemStack(LucenticsItemRegister.DAWNSTONE_DUST.asItem(), 1);
            Containers.dropItemStack(level, vec.x, vec.y, vec.z, dust);

            level.playSound(null, pos, SoundEvents.TUFF_BREAK, SoundSource.BLOCKS, 1.5f, 1f);
        } else {
            level.setBlock(pos, state.setValue(CRACKS, currentCracks), 3);
            level.playSound(null, pos, SoundEvents.TUFF_HIT, SoundSource.BLOCKS, 0.8f + 0.1f * currentCracks, 1f);
        }

        stack.hurtAndBreak(hammer.getDamagePerCrack(), player, LivingEntity.getSlotForHand(hand));

        return ItemInteractionResult.SUCCESS;
    }
}
