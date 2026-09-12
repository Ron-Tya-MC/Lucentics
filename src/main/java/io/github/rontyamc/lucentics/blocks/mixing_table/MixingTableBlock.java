package io.github.rontyamc.lucentics.blocks.mixing_table;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.blocks.IBlockEntities;
import io.github.rontyamc.lucentics.blocks.tank.light_copper_tank.TankLightCopperBlockEntity;
import io.github.rontyamc.lucentics.registers.LucenticsBlockEntityRegister;
import net.minecraft.core.BlockPos;
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
import net.neoforged.neoforge.fluids.FluidUtil;

public class MixingTableBlock extends BaseEntityBlock implements IBlockEntities<MixingTableBlockEntity> {
    public static final MapCodec<MixingTableBlock> CODEC = simpleCodec(MixingTableBlock::new);
    private static final VoxelShape BASE = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);

    private static final VoxelShape BOTTOM = Block.box(3.0, 4.0, 3.0, 13.0, 5.0, 13.0);

    private static final VoxelShape MIDDLE = Block.box(2.0, 5.0, 2.0, 14.0, 12.0, 14.0);
    private static final VoxelShape TOP = Block.box(1.0, 12.0, 1.0, 15.0, 14.0, 15.0);
    private static final VoxelShape COVER_BOTTOM = Block.box(2.0, 14.0, 2.0, 14.0, 15.0, 14.0);
    private static final VoxelShape COVER_TOP = Block.box(6.0, 15.0, 6.0, 10.0, 16.0, 10.0);

    private static final VoxelShape SHAPE = Shapes.or(BASE, BOTTOM, MIDDLE, TOP, COVER_BOTTOM, COVER_TOP);

    public MixingTableBlock(Properties properties) {super(properties);}

    @Override
    public Class<MixingTableBlockEntity> getBlockEntityClass() {
        return MixingTableBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MixingTableBlockEntity> getBlockEntityType() {
        return LucenticsBlockEntityRegister.MIXING_TABLE.get();
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
            if (level.getBlockEntity(pos) instanceof MixingTableBlockEntity be) {
                be.dropContents(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

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
        return new MixingTableBlockEntity(getBlockEntityType(), pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return IBlockEntities.createTickerHelper(type, LucenticsBlockEntityRegister.MILLING_TABLE.get(),
                (l, pos, s, be) -> be.tick(l, pos, s));
    }
}
