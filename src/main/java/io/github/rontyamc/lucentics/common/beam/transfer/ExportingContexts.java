package io.github.rontyamc.lucentics.common.beam.transfer;

import io.github.rontyamc.lucentics.common.beam.node.BeamNode;
import io.github.rontyamc.lucentics.common.dict.Warns;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class ExportingContexts {
    public static class ItemExportingContext {
        public record ItemExportingInfo(IItemAcceptor acceptor, ItemStack stack, BeamNode targetNode, ServerLevel level) {
            public IItemAcceptor.Result commit() {
                return acceptor.acceptItems(level, targetNode.pos(), stack, false);
            }
        }

        public static final ItemExportingContext EMPTY = new ItemExportingContext(List.of(), IItemAcceptor.Result.EMPTY, true);

        private final List<ItemExportingInfo> infos;
        private final IItemAcceptor.Result fallbacks;
        private boolean leftoverProcessed;

        public ItemExportingContext(List<ItemExportingInfo> infos, IItemAcceptor.Result fallbacks) {
            this.infos = infos;
            this.fallbacks = fallbacks;
            this.leftoverProcessed = false;
        }

        private ItemExportingContext(List<ItemExportingInfo> infos, IItemAcceptor.Result fallbacks, boolean leftoverProcessed) {
            this.infos = infos;
            this.fallbacks = fallbacks;
            this.leftoverProcessed = leftoverProcessed;
        }

        public List<ItemExportingInfo> infos() {
            return infos;
        }

        public IItemAcceptor.Result fallbacks() {
            return fallbacks;
        }

        public boolean hasAnyExport() {
            return !infos.isEmpty();
        }

        /**
         * Contextに登録されたAcceptor一つ一つに対して実際にアイテムを挿入し、Acceptorの返り値を纏めて送り返す。
         * このメソッドの実行前に同じContextに対して{@link ItemExportingContext#markProcessed()}を明示的に呼ぶ必要がある。
         *   (Acceptorに送られないアイテムが適切に処理されたかを確かめるため)
         *
         * @return 各Acceptorから送られてきた返り値のList
         */
        public List<IItemAcceptor.Result> commit() {
            if (!fallbacks.leftover().isEmpty() && !leftoverProcessed) {
                String caller = StackWalker.getInstance().walk(frames -> frames
                        .dropWhile(frame -> frame.getClassName().startsWith("io.github.rontyamc.lucentics.common.beam.transfer"))
                        .findFirst()
                        .map(frame -> frame.getClassName() + "#" + frame.getMethodName() + ":" + frame.getLineNumber())
                        .orElse("<unknown caller>"));
                Warns.UNPROCESSED_LEFTOVER_BEFORE_COMMIT.cast(caller, fallbacks.leftover());
            }

            List<IItemAcceptor.Result> results = new ArrayList<>();
            for (ItemExportingInfo info : infos) results.add(info.commit());
            return results;
        }

        /**
         * どこのAcceptorにも割り振られなかった余り物を適切に処理したのち、これを呼ぶ。
         * 呼び忘れると{@link ItemExportingContext#commit()}の呼び出し時に警告が発行される。
         */
        public void markProcessed() {
            this.leftoverProcessed = true;
        }
    }

    public static class FluidExportingContext {
        public record FluidExportingInfo(IFluidAcceptor acceptor, FluidStack stack, BeamNode targetNode, ServerLevel level) {
            public FluidStack commit() {
                return stack.copyWithAmount(acceptor.acceptFluids(level, targetNode.pos(), stack, false));
            }
        }

        public static final FluidExportingContext EMPTY = new FluidExportingContext(List.of(), FluidStack.EMPTY, true);

        private final List<FluidExportingInfo> infos;
        private final FluidStack leftover;
        private boolean leftoverProcessed;

        public FluidExportingContext(List<FluidExportingInfo> infos, FluidStack leftover) {
            this.infos = infos;
            this.leftover = leftover;
            this.leftoverProcessed = false;
        }

        private FluidExportingContext(List<FluidExportingInfo> infos, FluidStack leftover, boolean leftoverProcessed) {
            this.infos = infos;
            this.leftover = leftover;
            this.leftoverProcessed = leftoverProcessed;
        }

        public List<FluidExportingInfo> infos() {
            return infos;
        }

        public FluidStack leftover() {
            return leftover;
        }

        public boolean hasAnyExport() {
            return !infos.isEmpty();
        }

        /**
         * Contextに登録されたAcceptor一つ一つに対して実際に液体を挿入し、Acceptorの返り値を纏めて送り返す。
         * このメソッドの実行前に同じContextに対して{@link FluidExportingContext#markProcessed()}を明示的に呼ぶ必要がある。
         *   (Acceptorに送られない液体が適切に処理されたかを確かめるため)
         *
         * @return 各Acceptorから送られてきた返り値のList
         */
        public List<FluidStack> commit() {
            if (!leftover.isEmpty() && !leftoverProcessed) {
                String caller = StackWalker.getInstance().walk(frames -> frames
                        .dropWhile(frame -> frame.getClassName().startsWith("io.github.rontyamc.lucentics.common.beam.transfer"))
                        .findFirst()
                        .map(frame -> frame.getClassName() + "#" + frame.getMethodName() + ":" + frame.getLineNumber())
                        .orElse("<unknown caller>"));
                Warns.UNPROCESSED_LEFTOVER_BEFORE_COMMIT.cast(caller, leftover);
            }

            List<FluidStack> results = new ArrayList<>();
            for (FluidExportingInfo info : infos) results.add(info.commit());
            return results;
        }

        /**
         * どこのAcceptorにも割り振られなかった余り物を適切に処理したのち、これを呼ぶ。
         * 呼び忘れると{@link FluidExportingContext#commit()}の呼び出し時に警告が発行される。
         */
        public void markProcessed() {
            this.leftoverProcessed = true;
        }
    }
}
