package io.github.rontyamc.lucentics.common.beam.transfer;

import io.github.rontyamc.lucentics.common.beam.node.BeamNode;
import io.github.rontyamc.lucentics.common.beam.node.PrismQualifier;
import io.github.rontyamc.lucentics.common.beam.particle.BeamParticles;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts.FluidExportingContext;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts.FluidExportingContext.FluidExportingInfo;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts.ItemExportingContext;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts.ItemExportingContext.ItemExportingInfo;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.dict.Warns;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import io.github.rontyamc.lucentics.common.util.ParticleUtil;
import io.github.rontyamc.lucentics.common.util.TransferUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * デバイスに関する操作やutilにするには少し複雑な操作を{@link TransferUtil}から切り出したクラス。
 */
public class TransferManager {
    private TransferManager() {}

    public static final Supplier<Integer> MAX_RECURSION = () -> 32;

    public static Optional<BlockPos> resolveDevicePos(BeamNode node, ServerLevel level, PrismQualifier qualifier, boolean allowEndpoint) {
        if (node.isEndpoint() && allowEndpoint) return Optional.of(node.pos());

        BlockPos prismPos = node.pos();
        if (!qualifier.test(level, prismPos)) return Optional.empty();
        return Optional.of(prismPos.above());
    }

    public static Optional<BlockPos> resolveDevicePos(BeamNode node, ServerLevel level, PrismQualifier qualifier) {
        return resolveDevicePos(node, level, qualifier, false);
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

    private static boolean tryExportItems(ServerLevel level, IItemHandler source, List<BeamNode> nodesAhead, int maxAmount,
                                          BlockPos sourcePos, BlockPos sourceNodePos, Colors particleColor, int particleInterval) {
        int slot = TransferUtil.firstImportable(source).slot();
        if (slot == -1) return false;

        ItemStack simulated = source.extractItem(slot, maxAmount, true);
        if (simulated.isEmpty()) return false;

        ItemExportingContext context = planItemExport(level, simulated, nodesAhead);
        if (!context.hasAnyExport()) return false;

        int accepted = simulated.getCount() - context.fallbacks().leftover().getCount();
        ItemStack extracted = source.extractItem(slot, accepted, false);
        if (extracted.isEmpty()) return false;

        context.markProcessed(); // 余りは元から搬出されてないため問題なし
        commitAndFallback(context, results -> recursiveExportItem(results, context, level, source, nodesAhead, MAX_RECURSION.get()));

        if (particleColor != null && BeamParticles.canSpawnOnThisTick(level, sourceNodePos)) {
            for (ItemExportingInfo info : context.infos()) {
                ParticleUtil.spawnAndScheduleFlowing(level, sourcePos, sourceNodePos, info.targetNode(), particleColor, particleInterval);
            }
        }

        return true;
    }

    private static void recursiveExportItem(List<IItemAcceptor.Result> results, ItemExportingContext context, ServerLevel level, IItemHandler source, List<BeamNode> nodesAhead, int recursionLimit) {
        // InfoとResultは同順で1:1対応する
        for (int i = 0; i < results.size(); i++) {
            IItemAcceptor.Result result = results.get(i);
            ItemExportingInfo info = context.infos().get(i);

            if (!result.leftover().isEmpty()) {
                BeamNode infoNode = info.targetNode();

                Warns.UNEXPECTED_LEFTOVER_AFTER_COMMIT.cast(result.leftover(), infoNode.dimension(), infoNode.pos(), infoNode.blockId());
                TransferUtil.insertOrConsume(source, result.leftover(), leftover -> ItemUtil.dropItem(level, info.targetNode().pos().above(), leftover));
            }

            for (ItemStack byproduct : result.byproducts()) {
                int targetIndex = nodesAhead.indexOf(info.targetNode());
                List<BeamNode> remainingNodes = targetIndex >= 0 ? nodesAhead.subList(targetIndex + 1, nodesAhead.size()) : List.of();

                ItemExportingContext byproductContext = planItemExport(level, byproduct, remainingNodes);
                TransferUtil.insertOrConsume(source, byproductContext.fallbacks().leftover(), itemStack -> ItemUtil.dropItem(level, info.targetNode().pos(), itemStack));
                byproductContext.markProcessed();

                if (recursionLimit > 0) commitAndFallback(byproductContext, additionalResults -> recursiveExportItem(additionalResults, context, level, source, nodesAhead, recursionLimit - 1));
                else commitAndFallback(byproductContext, level, info.targetNode().pos().above());
            }
        }
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

        context.markProcessed(); // 余りは元から搬出されてないため問題なし
        commitAndFallback(context, leftovers -> {
            for (int i = 0; i < leftovers.size(); i++) {
                FluidStack leftover = leftovers.get(i);
                FluidExportingInfo info = context.infos().get(i);

                if (!leftover.isEmpty()) {
                    BeamNode infoNode = info.targetNode();

                    Warns.UNEXPECTED_LEFTOVER_AFTER_COMMIT.cast(leftover, infoNode.dimension(), infoNode.pos(), infoNode.blockId());
                    TransferUtil.insertOrConsume(source, leftover, vanish -> {});
                }
            }
        });

        if (particleColor != null && BeamParticles.canSpawnOnThisTick(level, sourceNodePos)) {
            for (FluidExportingInfo info : context.infos()) {
                ParticleUtil.spawnAndScheduleFlowing(level, sourcePos, sourceNodePos, info.targetNode(), particleColor, particleInterval);
            }
        }

        return true;
    }

    /**
     * contextをcommit()し、その返り値をConsumerに従って処理する。
     *
     * @param context commit()したいItemExportingContext
     * @param additionalFallbacksConsumer commit()の返り値に対して行いたい処理を記述したConsumer
     */
    public static void commitAndFallback(ItemExportingContext context, Consumer<List<IItemAcceptor.Result>> additionalFallbacksConsumer) {
        List<IItemAcceptor.Result> result = context.commit();
        additionalFallbacksConsumer.accept(result);
    }

    /**
     * {@link TransferManager#commitAndFallback(ItemExportingContext context, Consumer additionalFallbacksConsumer)}の簡易版。
     * context.commit()の返り値の中の余りアイテムを指定したlevel, BlockPosの場所に全てドロップさせる
     *
     * @param context commit()したいItemExportingContext
     * @param level アイテムをドロップさせるLevel
     * @param dropPos アイテムをドロップさせるBlockPos
     */
    public static void commitAndFallback(ItemExportingContext context, Level level, BlockPos dropPos) {
        commitAndFallback(context, results -> {
            for (int i = 0; i < results.size(); i++) {
                IItemAcceptor.Result result = results.get(i);

                if (!result.leftover().isEmpty()) {
                    BeamNode infoNode = context.infos().get(i).targetNode();

                    Warns.UNEXPECTED_LEFTOVER_AFTER_COMMIT.cast(result.leftover(), infoNode.dimension(), infoNode.pos(), infoNode.blockId());
                    ItemUtil.dropItem(level, dropPos, result.leftover());
                }

                if (!result.byproducts().isEmpty()) {
                    ItemUtil.dropItem(level, dropPos, result.byproducts());
                }
            }
        });
    }

    /**
     * contextをcommit()し、その返り値をConsumerに従って処理する。
     *
     * @param context commit()したいFluidExportingContext
     * @param additionalFallbacksConsumer commit()の返り値に対して行いたい処理を記述したConsumer
     */
    public static void commitAndFallback(FluidExportingContext context, Consumer<List<FluidStack>> additionalFallbacksConsumer) {
        List<FluidStack> result = context.commit();
        additionalFallbacksConsumer.accept(result);
    }

    /**
     * {@link TransferManager#commitAndFallback(FluidExportingContext context, Consumer additionalFallbacksConsumer)}の簡易版。
     * context.commit()で生じた予期せぬ余りを、警告を発行して消し飛ばす。
     *
     * @param context commit()したいFluidExportingContext
     */
    public static void commitAndFallback(FluidExportingContext context) {
        commitAndFallback(context, results -> {
            for (int i = 0; i < results.size(); i++) {
                FluidStack result = results.get(i);

                if (!result.isEmpty()) {
                    BeamNode infoNode = context.infos().get(i).targetNode();

                    Warns.UNEXPECTED_LEFTOVER_AFTER_COMMIT.cast(result, infoNode.dimension(), infoNode.pos(), infoNode.blockId());
                }
            }
        });
    }

    /**
     * ※plan時点ではItemExportingContext.fallbacks.byproductsは空で渡される。
     * 水を排出した後の空バケツなど、実際に動作させないと生まれない副産物もあるためである。
     * 必ず返り値のItemExportingContextを{@link ItemExportingContext#commit()}する際に返り値を確認すること。
     * {@link TransferManager#commitAndFallback(ItemExportingContext context, Consumer additionalFallbacksConsumer)}を使うことで、
     *   contextをcommitした上でその返り値であるList<IItemAcceptor.Result>に対する処理を指定できる。
      */
    public static ItemExportingContext planItemExport(ServerLevel level, ItemStack stack, List<BeamNode> nodesAhead) {
        List<ItemExportingInfo> infos = new ArrayList<>();
        ItemStack remaining = stack.copy();

        boolean stopExplore = false;

        for (BeamNode node : nodesAhead) {
            if (remaining.isEmpty() || stopExplore) break;

            if (PrismQualifier.STOP_EXPLORE.test(level, node.pos())) stopExplore = true;

            IItemAcceptor acceptor;

            if (PrismQualifier.ITEM_ACCEPTOR.test(level, node.pos())) {
                acceptor = IItemAcceptor.get(level, node.pos()).get();
            }
            else {
                Optional<BlockPos> targetPos = resolveDevicePos(node, level, PrismQualifier.EXPORT_TARGET, true);
                if (targetPos.isEmpty()) continue;

                Optional<IItemHandler> target = TransferUtil.getItemHandler(level, targetPos.get());
                if (target.isEmpty()) continue;

                acceptor = IItemAcceptor.ofHandler(target.get());
            }

            IItemAcceptor.Result result = acceptor.acceptItems(level, node.pos(), remaining, true);
            ItemStack leftover = result.leftover();

            int accepted = remaining.getCount() - leftover.getCount();
            if (accepted <= 0) continue;

            infos.add(new ItemExportingInfo(acceptor, remaining.copyWithCount(accepted), node, level));
            remaining = leftover.copy();
        }

        return new ItemExportingContext(List.copyOf(infos), new IItemAcceptor.Result(remaining, List.of()));
    }

    public static FluidExportingContext planFluidExport(ServerLevel level, FluidStack stack, List<BeamNode> nodesAhead) {
        List<FluidExportingInfo> infos = new ArrayList<>();
        FluidStack remaining = stack.copy();

        boolean stopExplore = false;

        for (BeamNode node : nodesAhead) {
            if (remaining.isEmpty() || stopExplore) break;

            if (PrismQualifier.STOP_EXPLORE.test(level, node.pos())) stopExplore = true;

            IFluidAcceptor acceptor;

            if (PrismQualifier.FLUID_ACCEPTOR.test(level, node.pos())) {
                acceptor = IFluidAcceptor.get(level, node.pos()).get();
            }
            else {
                Optional<BlockPos> targetPos = resolveDevicePos(node, level, PrismQualifier.EXPORT_TARGET, true);
                if (targetPos.isEmpty()) continue;

                Optional<IFluidHandler> target = TransferUtil.getFluidHandler(level, targetPos.get());
                if (target.isEmpty()) continue;

                acceptor = IFluidAcceptor.ofHandler(target.get());
            }

            int accepted = acceptor.acceptFluids(level, node.pos(), remaining, true);
            if (accepted <= 0) continue;

            infos.add(new FluidExportingInfo(acceptor, remaining.copyWithAmount(accepted), node, level));
            remaining = remaining.copyWithAmount(remaining.getAmount() - accepted);
        }

        return new FluidExportingContext(List.copyOf(infos), remaining);
    }

    public record TransferRate(Optional<Integer> itemTransferRate, Optional<Integer> fluidTransferRate) {
        public static final TransferRate DEFAULT = new TransferRate(Optional.of(32), Optional.of(1000));

        public boolean canTransferItem() {
            return itemTransferRate.isPresent() && itemTransferRate.get() > 0;
        }

        public boolean canTransferFluid() {
            return fluidTransferRate.isPresent() && fluidTransferRate.get() > 0;
        }

        // 将来電力等に対応するなら、ここに追加
    }
}
