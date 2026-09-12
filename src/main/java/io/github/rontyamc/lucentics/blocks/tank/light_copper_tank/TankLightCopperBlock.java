package io.github.rontyamc.lucentics.blocks.tank.light_copper_tank;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.IBlockEntities;
import io.github.rontyamc.lucentics.blocks.tank.TankBlockEntity;
import io.github.rontyamc.lucentics.registers.LucenticsBlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;

public class TankLightCopperBlock extends BaseEntityBlock implements IBlockEntities<TankBlockEntity> {
    public static final MapCodec<TankLightCopperBlock> CODEC = simpleCodec(TankLightCopperBlock::new);

    @Override
    public Class<TankBlockEntity> getBlockEntityClass() {
        return TankBlockEntity.class;
    }
    @Override
    public BlockEntityType<? extends TankBlockEntity> getBlockEntityType() {
        return LucenticsBlockEntityRegister.TANK_LIGHT_COPPER.get();
    }

    public TankLightCopperBlock(Properties properties) {super(properties);}

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {return RenderShape.MODEL;}

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof TankLightCopperBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        var result = FluidUtil.interactWithFluidHandler(player, interactionHand, level, pos, hitResult.getDirection());
        if (result) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TankLightCopperBlockEntity(getBlockEntityType(), pos, state);
    }
}
