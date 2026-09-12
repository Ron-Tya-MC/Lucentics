package io.github.rontyamc.lucentics.common;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.function.Supplier;

import static net.neoforged.neoforge.fluids.FluidStack.isSameFluid;

public class FluidSlot {
    private FluidStack content = FluidStack.EMPTY;
    private final Supplier<Integer> capacity;

    public FluidSlot(FluidStack content, int capacity) {
        this(content, () -> capacity);
    }

    public FluidSlot(FluidStack content, Supplier<Integer> capacity) {
        this.content = content;
        this.capacity = capacity;
    }

    public FluidSlot(int capacity) {
        this(() -> capacity);
    }

    public FluidSlot(Supplier<Integer> capacity) {
        this.capacity = capacity;
    }

    public static FluidSlot of(FluidStack content, int capacity) {
        return new FluidSlot(content, () -> capacity);
    }

    public static FluidSlot of(FluidStack content, Supplier<Integer> capacity) {
        return new FluidSlot(content, capacity);
    }

    public FluidStack getContent() {
        return content;
    }

    public void setContent(FluidStack content) {
        this.content = content;
    }

    public int getCapacity() {
        return capacity.get();
    }

    public void clear() {
        content = FluidStack.EMPTY;
    }

    public int getRemainingSpace() {
        return content.isEmpty() ? getCapacity() : getCapacity() - content.getAmount();
    }

    public static List<FluidStack> stackList(List<FluidSlot> slots) {
        return slots.stream().map(FluidSlot::getContent).toList();
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) return 0;
        if (!content.isEmpty() && !isSameFluid(content, resource)) return 0;

        int remainingSpace = getRemainingSpace();
        if (remainingSpace <= 0) return 0;

        int filledAmount = Math.min(remainingSpace, resource.getAmount());

        if (action.execute()) {
            content = content.isEmpty()
                    ? resource.copyWithAmount(filledAmount)
                    : content.copyWithAmount(content.getAmount() + filledAmount);
        }

        return filledAmount;
    }

    public FluidStack drain(FluidStack stack, IFluidHandler.FluidAction action) {
        if (stack.isEmpty() || content.isEmpty()) return FluidStack.EMPTY;
        if (!isSameFluid(content, stack)) return FluidStack.EMPTY;

        return drain(stack.getAmount(), action);
    }

    public FluidStack drain(int amount, IFluidHandler.FluidAction action) {
        if (content.isEmpty() || amount <= 0) return FluidStack.EMPTY;

        FluidStack copyStack = content.copy();
        FluidStack drained = copyStack.split(amount);

        if (action.execute()) {
            content = copyStack;
        }

        return drained;
    }

    public void write(CompoundTag nbt, HolderLookup.Provider registries, String key) {
        if (content.isEmpty()) return;
        FluidStack.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), content)
                .result().ifPresent(tag -> nbt.put(key, tag));
    }

    public void read(CompoundTag nbt, HolderLookup.Provider registries, String key) {
        content = nbt.contains(key)
                ? FluidStack.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), nbt.get(key)).result().orElse(FluidStack.EMPTY)
                : FluidStack.EMPTY;
    }
}
