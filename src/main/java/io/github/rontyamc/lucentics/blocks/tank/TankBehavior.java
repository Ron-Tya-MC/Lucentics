package io.github.rontyamc.lucentics.blocks.tank;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.function.Supplier;

import static net.neoforged.neoforge.fluids.FluidStack.isSameFluid;

public class TankBehavior extends BlockEntityBehavior implements Clearable {
    public static final BehaviorType<TankBehavior> TYPE = new BehaviorType<>("tank");

    private FluidStack container = FluidStack.EMPTY;
    private Supplier<Integer> capacity;
    public TankFHandler fHandler;

    public TankBehavior(BaseBlockEntity be, int capacity) {
        super(be);

        this.capacity = () -> capacity;
        fHandler = new TankFHandler(this);
        clearContent();
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    public FluidStack getContainer() {
        return container;
    }

    @Override
    public void clearContent() {
        container = FluidStack.EMPTY;
    }

    public int getCapacity() {
        return capacity.get();
    }

    public int getRemainingSpace() {
        if (getContainer().isEmpty()) return getCapacity();
        return getCapacity() - getContainer().getAmount();
    }

    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        if (!getContainer().isEmpty() && !isSameFluid(getContainer(), resource)) return 0;

        int remainingSpace = getRemainingSpace();
        if (remainingSpace <= 0) return 0;

        int filledAmount = Math.min(remainingSpace, resource.getAmount());

        if (action.execute()) {
            if (getContainer().isEmpty()) {
                container = resource.copyWithAmount(filledAmount);
            } else {
                container.setAmount(container.getAmount() + filledAmount);
            }
            blockEntity.updated();
        }

        return filledAmount;
    }

    public FluidStack drain(FluidStack stack, FluidAction action) {
        if (stack.isEmpty() || getContainer().isEmpty()) return FluidStack.EMPTY;
        if (!isSameFluid(getContainer(), stack)) return FluidStack.EMPTY;

        return drain(stack.getAmount(), action);
    }

    public FluidStack drain(int maxDrain, FluidAction action) {
        if (getContainer().isEmpty() || maxDrain <= 0) return FluidStack.EMPTY;

        int drainedAmount = Math.min(maxDrain, getContainer().getAmount());
        FluidStack drained = getContainer().copyWithAmount(drainedAmount);

        if (action.execute()) {
            int remaining = getContainer().getAmount() - drainedAmount;
            container = remaining <= 0 ? FluidStack.EMPTY : container;
            if (remaining > 0) container.setAmount(remaining);
            blockEntity.updated();
        }

        return drained;
    }

    public void dropContents(Level level, BlockPos pos) {
        clearContent();
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if (!getContainer().isEmpty()) {
            FluidStack.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), getContainer())
                    .result().ifPresent(tag -> nbt.put("container", tag));
        }
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        container = nbt.contains("container")
                ? FluidStack.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), nbt.get("container")).result().orElse(FluidStack.EMPTY)
                : FluidStack.EMPTY;
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }
}
