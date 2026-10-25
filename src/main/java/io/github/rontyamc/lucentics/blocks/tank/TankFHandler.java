package io.github.rontyamc.lucentics.blocks.tank;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class TankFHandler implements IFluidHandler {
    private final TankBehavior behavior;

    public TankFHandler(TankBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return behavior.getContent();
    }

    @Override
    public int getTankCapacity(int tank) {
        return behavior.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
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
