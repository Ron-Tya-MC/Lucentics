package io.github.rontyamc.lucentics.blocks.tank;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.FluidSlot;
import io.github.rontyamc.lucentics.common.ThingStack;
import io.github.rontyamc.lucentics.common.beam.INodeDevice;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class TankBehavior extends BlockEntityBehavior implements INodeDevice, Clearable {
    public static final BehaviorType<TankBehavior> TYPE = new BehaviorType<>("tank");

    private final FluidSlot tank;
    public TankFHandler fHandler;

    public TankBehavior(BaseBlockEntity be, int capacity) {
        super(be);

        this.tank = new FluidSlot(capacity);
        fHandler = new TankFHandler(this);
        clearContent();
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    public FluidSlot getTank() {
        return tank;
    }

    @Override
    public ThingStack getStack() {
        return ThingStack.of(tank.getContent());
    }

    public FluidStack getContent() {
        return tank.getContent();
    }

    @Override
    public void clearContent() {
        tank.clear();
    }

    public int getCapacity() {
        return tank.getCapacity();
    }

    public int getRemainingSpace() {
        if (getContent().isEmpty()) return getCapacity();
        return getCapacity() - getContent().getAmount();
    }

    public int fill(FluidStack resource, FluidAction action) {
        int filledAmount = tank.fill(resource, action);
        if (filledAmount > 0 && action.execute()) blockEntity.updated();
        return filledAmount;
    }

    public FluidStack drain(FluidStack stack, FluidAction action) {
        FluidStack drained = tank.drain(stack, action);
        if (action.execute()) blockEntity.updated();
        return drained;
    }

    public FluidStack drain(int amount, IFluidHandler.FluidAction action) {
        FluidStack drained = tank.drain(amount, action);
        if (action.execute()) blockEntity.updated();
        return drained;
    }

    public void dropContents(Level level, BlockPos pos) {
        clearContent();
    }

    @Override
    public void consume(int amount) {
        if (getContent().isEmpty()) return;
        tank.getContent().shrink(amount);
        blockEntity.updated();
    }

    @Override
    public void damageItem(int damage) {}

    @Override
    public void catalyst() {}

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        tank.write(nbt, registries, "container");
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        tank.read(nbt, registries, "container");
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }
}
