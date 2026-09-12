package io.github.rontyamc.lucentics.blocks.mixing_table;

import io.github.rontyamc.lucentics.common.util.FluidUtil;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MixingTableFHandler implements IFluidHandler {
    private final MixingTableBehavior behavior;

    public MixingTableFHandler(MixingTableBehavior behavior) {
        this.behavior = behavior;
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
        return behavior.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        if (tank != 0) return false;
        return behavior.getContainer().isEmpty() || FluidUtil.isSameFluid(behavior.getContainer().asFluidOrEmpty(), stack, false);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return behavior.fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return behavior.drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return behavior.drain(maxDrain, action);
    }
}
