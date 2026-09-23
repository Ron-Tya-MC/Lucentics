package io.github.rontyamc.lucentics.blocks.mixing_table;

import io.github.rontyamc.lucentics.common.util.FluidUtil;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MixingTableFHandler implements IFluidHandler {
    private final MixingTableBehavior behavior;
    private final boolean accessLockedContainer;

    public MixingTableFHandler(MixingTableBehavior behavior) {
        this.behavior = behavior;
        this.accessLockedContainer = false;
    }

    public MixingTableFHandler(MixingTableBehavior behavior, boolean accessLockedContainer) {
        this.behavior = behavior;
        this.accessLockedContainer = accessLockedContainer;
    }

    @Override
    public int getTanks() {
        return behavior.getBuffer().size() + 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return tank == 0 ? behavior.getContainer().asFluidOrEmpty() : behavior.getBufferAt(tank - 1);
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? behavior.getCapacityContainer() : behavior.getCapacityBuffer();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        if (tank == 0) return false;
        return behavior.getBufferAt(tank - 1).isEmpty() || FluidUtil.isSameFluid(behavior.getBufferAt(tank - 1), stack, false);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return behavior.fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack drained = FluidStack.EMPTY;
        FluidStack draining = resource;

        if (behavior.hasOutputItem() || accessLockedContainer) {
            drained = FluidUtil.merge(drained, behavior.drain(draining, action)).merged();
            if (drained.getAmount() >= resource.getAmount()) return drained;
            else draining = draining.copyWithAmount(draining.getAmount() - drained.getAmount());
        }
        for (int i = 0; i < behavior.getBuffer().size(); i++) {
            drained = FluidUtil.merge(drained, behavior.drainBufferAt(i, draining, action)).merged();
            if (drained.getAmount() >= resource.getAmount()) return drained;
            else draining = draining.copyWithAmount(draining.getAmount() - drained.getAmount());
        }

        return drained;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return (behavior.hasOutputItem() || accessLockedContainer) ? behavior.drain(maxDrain, action) : behavior.drainBufferAt(0, maxDrain, action);
    }
}
