package io.github.rontyamc.lucentics.common.beam.transfer;

import io.github.rontyamc.lucentics.common.beam.node.BeamNode;
import io.github.rontyamc.lucentics.common.beam.node.PrismQualifier;
import io.github.rontyamc.lucentics.common.beam.particle.BeamParticles;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts.FluidExportingContext;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts.FluidExportingContext.FluidExportingInfo;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts.ItemExportingContext;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts.ItemExportingContext.ItemExportingInfo;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.util.ParticleUtil;
import io.github.rontyamc.lucentics.common.util.TransferUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * デバイスに関する操作やutilにするには少し複雑な操作を{@link TransferUtil}から切り出したクラス。
 */
public class TransferManager {
    private TransferManager() {}

    public static Optional<BlockPos> resolveDevicePos(BeamNode node, ServerLevel level, PrismQualifier qualifier, boolean allowEndpoint) {
        if (node.isEndpoint() && allowEndpoint) return Optional.of(node.pos());

        BlockPos prismPos = node.pos();
        if (!qualifier.test(level, prismPos)) return Optional.empty();
        return Optional.of(prismPos.above());
    }

    public static Optional<BlockPos> resolveDevicePos(BeamNode node, ServerLevel level, PrismQualifier qualifier) {
        return resolveDevicePos(node, level, qualifier, false);
    }

    public static int transferItems(IItemHandler source, IItemHandler target, int maxCount) {
        int sourceSlot = -1;
        ItemStack simulated = ItemStack.EMPTY;

        for (int i = 0; i < source.getSlots(); i++) {
            if (source.getStackInSlot(i).isEmpty()) {
                continue;
            }

            simulated = source.extractItem(i, maxCount, true);
            if (simulated.isEmpty()) continue;

            sourceSlot = i;
            break;
        }
        if (sourceSlot == -1) return 0;

        ItemStack simulatedLeftover = ItemHandlerHelper.insertItemStacked(target, simulated, true);
        int accepted = simulated.getCount() - simulatedLeftover.getCount();
        if (accepted <= 0) return 0;

        ItemStack extracted = source.extractItem(sourceSlot, accepted, false);
        if (extracted.isEmpty()) return 0;

        ItemStack extractedLeftover = ItemHandlerHelper.insertItemStacked(target, extracted, false);
        int transferred = extracted.getCount() - extractedLeftover.getCount();
        if (!extractedLeftover.isEmpty()) {
            // simulateに応じて返り値を変えるやばいブロックが無い限りいらない
            ItemHandlerHelper.insertItemStacked(source, extractedLeftover, false);
        }
        return transferred;
    }

    public static int transferFluids(IFluidHandler source, IFluidHandler target, int maxAmount) {
        FluidStack simulatedDrain = source.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
        if (simulatedDrain.isEmpty()) return 0;

        int simulatedFill = target.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);
        if (simulatedFill <= 0) return 0;

        FluidStack drained = source.drain(simulatedFill, IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty()) return 0;

        int filled = target.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        if (filled < drained.getAmount()) {
            FluidStack leftover = drained.copyWithAmount(drained.getAmount() - filled);
            source.fill(leftover, IFluidHandler.FluidAction.EXECUTE);
        }
        return filled;
    }

    public static boolean tryExport(ServerLevel level, BlockPos sourcePos, List<BeamNode> nodesAhead, TransferRate rate,
                                    BlockPos sourceNodePos, Colors particleColor, int particleInterval) {
        Optional<IItemHandler> sourceIHandler = TransferUtil.getItemHandler(level, sourcePos);
        Optional<IFluidHandler> sourceFHandler = TransferUtil.getFluidHandler(level, sourcePos);

        boolean transferredItem = false;
        boolean transferredFluid = false;

        if (rate.canTransferItem() && sourceIHandler.isPresent() && TransferUtil.hasItem(sourceIHandler.get())) {
            transferredItem = tryExportItems(level, sourceIHandler.get(), nodesAhead, rate.itemTransferRate().get(),
                    sourcePos, sourceNodePos, particleColor, particleInterval);
        }

        if (rate.canTransferFluid() && sourceFHandler.isPresent() && TransferUtil.hasFluid(sourceFHandler.get())) {
            transferredFluid = tryExportFluids(level, sourceFHandler.get(), nodesAhead, rate.fluidTransferRate().get(),
                    sourcePos, sourceNodePos, particleColor, particleInterval);
        }

        return transferredItem || transferredFluid;
    }

    public static boolean tryExport(ServerLevel level, BlockPos sourcePos, List<BeamNode> nodesAhead, TransferRate rate) {
        return tryExport(level, sourcePos, nodesAhead, rate, sourcePos, null, 0);
    }

    private static boolean tryExportItems(ServerLevel level, IItemHandler source, List<BeamNode> nodesAhead, int maxAmount,
                                          BlockPos sourcePos, BlockPos sourceNodePos, Colors particleColor, int particleInterval) {
        int slot = TransferUtil.firstImportable(source).slot();
        if (slot == -1) return false;

        ItemStack simulated = source.extractItem(slot, maxAmount, true);
        if (simulated.isEmpty()) return false;

        ItemExportingContext context = planItemExport(level, simulated, nodesAhead);
        if (!context.hasAnyExport()) return false;

        int accepted = simulated.getCount() - context.leftover().getCount();
        ItemStack extracted = source.extractItem(slot, accepted, false);
        if (extracted.isEmpty()) return false;

        context.commit();

        if (particleColor != null && BeamParticles.canSpawnOnThisTick(level, sourceNodePos)) {
            for (ItemExportingInfo info : context.infos()) {
                ParticleUtil.spawnAndScheduleFlowing(level, sourcePos, sourceNodePos, info.targetNode(), particleColor, particleInterval);
            }
        }

        return true;
    }

    private static boolean tryExportFluids(ServerLevel level, IFluidHandler source, List<BeamNode> nodesAhead, int maxAmount,
                                           BlockPos sourcePos, BlockPos sourceNodePos, Colors particleColor, int particleInterval) {
        FluidStack simulatedDrain = source.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
        if (simulatedDrain.isEmpty()) return false;

        FluidExportingContext context = planFluidExport(level, simulatedDrain, nodesAhead);
        if (!context.hasAnyExport()) return false;

        int accepted = simulatedDrain.getAmount() - context.leftover().getAmount();
        FluidStack drained = source.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty()) return false;

        context.commit();

        if (particleColor != null && BeamParticles.canSpawnOnThisTick(level, sourceNodePos)) {
            for (FluidExportingInfo info : context.infos()) {
                ParticleUtil.spawnAndScheduleFlowing(level, sourcePos, sourceNodePos, info.targetNode(), particleColor, particleInterval);
            }
        }

        return true;
    }

    public static ItemExportingContext planItemExport(ServerLevel level, ItemStack stack, List<BeamNode> nodesAhead) {
        List<ItemExportingInfo> infos = new ArrayList<>();
        ItemStack remaining = stack.copy();

        for (BeamNode node : nodesAhead) {
            if (remaining.isEmpty()) break;

            Optional<BlockPos> targetPos = resolveDevicePos(node, level, PrismQualifier.EXPORT_TARGET, true);
            if (targetPos.isEmpty()) continue;

            Optional<IItemHandler> target = TransferUtil.getItemHandler(level, targetPos.get());
            if (target.isEmpty()) continue;

            IItemAcceptor acceptor = IItemAcceptor.of(target.get());
            ItemStack leftover = acceptor.insert(remaining, true);
            int accepted = remaining.getCount() - leftover.getCount();
            if (accepted <= 0) continue;

            infos.add(new ItemExportingInfo(acceptor, remaining.copyWithCount(accepted), node));
            remaining = leftover;
        }

        return new ItemExportingContext(List.copyOf(infos), remaining);
    }

    public static FluidExportingContext planFluidExport(ServerLevel level, FluidStack stack, List<BeamNode> nodesAhead) {
        List<FluidExportingInfo> infos = new ArrayList<>();
        FluidStack remaining = stack.copy();

        for (BeamNode node : nodesAhead) {
            if (remaining.isEmpty()) break;

            Optional<BlockPos> targetPos = resolveDevicePos(node, level, PrismQualifier.EXPORT_TARGET, true);
            if (targetPos.isEmpty()) continue;

            Optional<IFluidHandler> target = TransferUtil.getFluidHandler(level, targetPos.get());
            if (target.isEmpty()) continue;

            IFluidAcceptor acceptor = IFluidAcceptor.of(target.get());
            int accepted = acceptor.fill(remaining, true);
            if (accepted <= 0) continue;

            infos.add(new FluidExportingInfo(acceptor, remaining.copyWithAmount(accepted), node));
            remaining = remaining.copyWithAmount(remaining.getAmount() - accepted);
        }

        return new FluidExportingContext(List.copyOf(infos), remaining);
    }

    public record TransferRate(Optional<Integer> itemTransferRate, Optional<Integer> fluidTransferRate) {
        public static final TransferRate DEFAULT = new TransferRate(Optional.of(16), Optional.of(500));

        public boolean canTransferItem() {
            return itemTransferRate.isPresent() && itemTransferRate.get() > 0;
        }

        public boolean canTransferFluid() {
            return fluidTransferRate.isPresent() && fluidTransferRate.get() > 0;
        }

        // 将来電力等に対応するなら、ここに追加
    }
}
