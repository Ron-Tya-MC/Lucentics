package io.github.rontyamc.lucentics.common;

import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class SlotInteractions {
    public interface SingleItemSlot {
        ItemStack getStack();
        ItemStack insert(ItemStack stack, boolean simulate);
        ItemStack extract(int amount, boolean simulate);
        int getRemainingSpace();
    }

    public enum Outcome {
        NONE, INSERTED, EXTRACTED, MERGED, SWAPPED
    }

    public record Result(Outcome outcome, ItemStack resultStack, Optional<ItemStack> fallbackStack) {
        public static final Result NONE = new Result(Outcome.NONE, ItemStack.EMPTY, Optional.empty());

        private static Result of(Outcome outcome, ItemStack resultStack) {
            return new Result(outcome, resultStack, Optional.empty());
        }
    }

    public static Result handle(SingleItemSlot slot, ItemStack heldStack, boolean simulate) {
        ItemStack current = slot.getStack();

        if (current.isEmpty()) {
            if (heldStack.isEmpty()) return Result.NONE;
            ItemStack remainder = slot.insert(heldStack.copy(), simulate);
            if (remainder.getCount() == heldStack.getCount()) return Result.NONE;
            return Result.of(Outcome.INSERTED, remainder);
        }

        if (heldStack.isEmpty()) {
            ItemStack extracted = slot.extract(current.getMaxStackSize(), simulate);
            return extracted.isEmpty() ? Result.NONE : Result.of(Outcome.EXTRACTED, extracted);
        }

        if (ItemUtilities.canStackItems(current, heldStack)) {
            int mergeCount = Math.min(slot.getRemainingSpace(), heldStack.getCount());
            if (mergeCount <= 0) return Result.NONE;
            slot.insert(heldStack.copyWithCount(mergeCount), simulate);
            ItemStack leftover = heldStack.copyWithCount(heldStack.getCount() - mergeCount);
            return Result.of(Outcome.MERGED, leftover);
        }

        ItemStack extracted = slot.extract(current.getCount(), simulate);
        ItemStack inserted = slot.insert(heldStack.copy(), simulate);

        if (inserted.isEmpty()) {
            return Result.of(Outcome.SWAPPED, extracted);
        }
        else {
            return new Result(Outcome.SWAPPED, inserted, Optional.of(extracted));
        }
    }
}
