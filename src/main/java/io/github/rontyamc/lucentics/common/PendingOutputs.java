package io.github.rontyamc.lucentics.common;

import io.github.rontyamc.lucentics.Lucentics;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class PendingOutputs {
    private ResourceLocation recipeId;
    private final List<ThingStack> contents;

    private static final PendingOutputs EMPTY = new PendingOutputs(Lucentics.defaultLocation( "empty"), new ArrayList<>());

    public PendingOutputs(ResourceLocation recipeId, List<ThingStack> contents) {
        this.recipeId = recipeId;
        this.contents = contents;
    }

    public static PendingOutputs of(ResourceLocation recipeId, List<ThingStack> stacks) {
        return new PendingOutputs(recipeId, stacks);
    }

    public static PendingOutputs of(ResourceLocation recipeId, ThingStack... stacks) {
        return new PendingOutputs(recipeId, List.of(stacks));
    }

    public static PendingOutputs empty() {
        return new PendingOutputs(Lucentics.defaultLocation( "empty"), new ArrayList<>());
    }

    public ResourceLocation recipeId() {
        return recipeId;
    }

    public List<ThingStack> contents() {
        return new ArrayList<>(contents);
    }

    public boolean isEmpty() {
        return this.recipeId.equals(EMPTY.recipeId) && this.contents.isEmpty();
    }

    public void clear() {
        this.recipeId = EMPTY.recipeId;
        this.contents.clear();
    }

    public void setRecipeId(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    public void add(ThingStack thingStack) {
        this.contents.add(thingStack);
    }

    public void add(ItemStack itemStack) {
        this.contents.add(ThingStack.of(itemStack));
    }

    public void add(FluidStack fluidStack) {
        this.contents.add(ThingStack.of(fluidStack));
    }

    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if (!recipeId.equals(EMPTY.recipeId)) nbt.putString("recipe_id", recipeId.toString());

        ListTag pendingList = new ListTag();
        for (ThingStack stack : this.contents) {
            if (stack.isItem()) {
                CompoundTag stackTag = new CompoundTag();
                stackTag.put("content", stack.asItemOrEmpty().save(registries, stackTag));
                stackTag.putString("type", "item");
                pendingList.add(stackTag);
            }
            else if (stack.isFluid()) {
                CompoundTag stackTag = new CompoundTag();
                stackTag.put("content", stack.asFluidOrEmpty().save(registries, stackTag));
                stackTag.putString("type", "fluid");
                pendingList.add(stackTag);
            }
            else {
                CompoundTag stackTag = new CompoundTag();
                stackTag.putString("type", "empty");
                pendingList.add(stackTag);
            }
        }

        if (!pendingList.isEmpty()) {
            nbt.put("contents", pendingList);
        }
    }

    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        this.recipeId = nbt.contains("recipe_id") ? ResourceLocation.parse(nbt.getString("recipe_id")) : EMPTY.recipeId;

        this.contents.clear();
        if (nbt.contains("contents")) {
            ListTag pendingList = nbt.getList("contents", net.minecraft.nbt.Tag.TAG_COMPOUND);
            for (int i = 0; i < pendingList.size(); i++) {
                CompoundTag stackTag = pendingList.getCompound(i);
                if (!stackTag.contains("type") || !stackTag.contains("content")) continue;

                if (stackTag.getString("type").equals("item"))
                    ItemStack.parse(registries, stackTag.getCompound("content"))
                            .ifPresent(item -> this.add(ThingStack.of(item)));

                if (stackTag.getString("type").equals("fluid"))
                    FluidStack.parse(registries, stackTag.getCompound("content"))
                            .ifPresent(fluid -> this.add(ThingStack.of(fluid)));
            }
        }
    }
}
