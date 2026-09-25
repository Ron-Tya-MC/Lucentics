package io.github.rontyamc.lucentics.blocks.prism.prism_fluid_place;

import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.common.beam.node.NodeActivationContext;
import io.github.rontyamc.lucentics.common.beam.transfer.*;
import io.github.rontyamc.lucentics.common.util.TransferUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;
import java.util.Optional;

public class PrismFluidPlaceBehavior extends PrismBehavior implements IItemAcceptor, IFluidAcceptor {
    public static final PrismFluidPlaceBehavior INSTANCE = new PrismFluidPlaceBehavior();

    protected PrismFluidPlaceBehavior() {}

    @Override
    public void activate(ServerLevel level, BlockPos prismPos, BlockPos interactPos, NodeActivationContext context) {}

    @Override
    public IItemAcceptor.Result acceptItems(ServerLevel level, BlockPos acceptorPos, ItemStack stack, boolean simulate) {
        Optional<IFluidHandlerItem> handler = TransferUtil.getFluidHandlerItem(stack);
        if (handler.isEmpty() || handler.get().getTanks() < 1) return Result.of(stack.copy());

        ImportingContexts.FluidImportingContext context = TransferUtil.firstImportableWithAmount(handler.get(), FluidType.BUCKET_VOLUME);
        if (context.isEmpty()) return Result.of(stack.copy());

        FluidStack simulated = context.handler().drain(context.fluidStack().copyWithAmount(FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
        if (simulated.isEmpty() || acceptFluids(level, acceptorPos, simulated, true) <= 0) return Result.of(stack.copy());

        if (!simulate) {
            FluidStack extracted = context.handler().drain(context.fluidStack().copyWithAmount(FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
            if (extracted.isEmpty() || extracted.getAmount() < FluidType.BUCKET_VOLUME) {
                context.handler().fill(extracted, IFluidHandler.FluidAction.EXECUTE);
                return Result.of(stack.copy());
            }
            if (acceptFluids(level, acceptorPos, extracted, false) <= 0) {
                context.handler().fill(extracted, IFluidHandler.FluidAction.EXECUTE);
                return Result.of(stack.copy());
            }
        }
        return new Result(stack.copyWithCount(stack.getCount() - 1), List.of(handler.get().getContainer()));
    }

    @Override
    public int acceptFluids(ServerLevel level, BlockPos acceptorPos, FluidStack stack, boolean simulate) {
        if (stack.getAmount() < FluidType.BUCKET_VOLUME) return 0;

        BlockPos interactPos = acceptorPos.above();
        BlockState state = level.getBlockState(interactPos);

        Fluid fluid = stack.getFluid();

        if (state instanceof LiquidBlockContainer liquidBlockContainer) {
            boolean canPlace = liquidBlockContainer.canPlaceLiquid(null, level, interactPos, state, fluid);
            if (!canPlace) return 0;

            if (simulate) {
                return FluidType.BUCKET_VOLUME;
            }
            else {
                return liquidBlockContainer.placeLiquid(level, interactPos, state, fluid.defaultFluidState())
                        ? FluidType.BUCKET_VOLUME : 0;
            }
        }
        else {
            boolean canPlace = state.canBeReplaced(fluid);
            if (!canPlace) return 0;

            if (simulate) {
                return FluidType.BUCKET_VOLUME;
            }
            else {
                level.destroyBlock(interactPos, true);
                return level.setBlockAndUpdate(interactPos, fluid.defaultFluidState().createLegacyBlock())
                        ? FluidType.BUCKET_VOLUME : 0;
            }
        }
    }
}
