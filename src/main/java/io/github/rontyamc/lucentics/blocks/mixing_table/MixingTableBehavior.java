package io.github.rontyamc.lucentics.blocks.mixing_table;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.FluidSlot;
import io.github.rontyamc.lucentics.common.ThingStack;
import io.github.rontyamc.lucentics.common.beam.Beam;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.TrailCraftingBehavior;
import io.github.rontyamc.lucentics.common.util.FluidUtil;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipeInput;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class MixingTableBehavior extends TrailCraftingBehavior implements Clearable {
    public static final BehaviorType<MixingTableBehavior> TYPE = new BehaviorType<>("mixing_table");

    public static final int CAPACITY_CONTAINER = 8 * FluidType.BUCKET_VOLUME;
    public static final int CAPACITY_BUFFER = 4 * FluidType.BUCKET_VOLUME;

    private final RandomSource randomSource = RandomSource.create();

    private final FluidSlot container;
    private final List<FluidSlot> buffer = new ArrayList<>();
    private boolean hasOutputItem;
    private final Supplier<Integer> maxBufferSize;
    public MixingTableFHandler fHandler;
    private boolean blockMerge;

    public MixingTableBehavior(BaseBlockEntity be) {
        super(be);

        hasOutputItem = false;
        this.container = new FluidSlot(CAPACITY_CONTAINER);
        maxBufferSize = () -> 1;
        setBlockMerge(true);
        fHandler = new MixingTableFHandler(this);
        clearContent();
    }

    public void setBlockMerge(boolean blockMerge) {
        this.blockMerge = blockMerge;
    }

    public boolean getBlockMerge() {
        return blockMerge && !hasOutputItem;
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    public FluidSlot getContainerSlot(){
        return container;
    }

    public List<FluidSlot> getBufferSlot(){
        return buffer;
    }

    @Override
    public ThingStack getContainer(){
        return ThingStack.of(container.getContent());
    }

    @Override
    public void setContainer(ThingStack stack) {
        container.setContent(stack.asFluidOrEmpty());
    }

    public List<ThingStack> getBuffer() {
        return ThingStack.fromFluids(FluidSlot.stackList(buffer));
    }

    public FluidStack getBufferAt(int index) {
        if (index < 0 || index >= buffer.size()) return FluidStack.EMPTY;
        return buffer.get(index) == null ? FluidStack.EMPTY : buffer.get(index).getContent();
    }

    public List<FluidStack> collectBuffer() {
        List<FluidStack> collected = new ArrayList<>(FluidSlot.stackList(buffer));
        buffer.clear();
        blockEntity.updated();
        return collected;
    }

    @Override
    public boolean hasOutputItem() {
        return hasOutputItem;
    }

    @Override
    public void setHasOutputItem(boolean hasOutputItem) {
        this.hasOutputItem = hasOutputItem;
    }

    public int getCapacity() {
        return container.getCapacity();
    }

    public Supplier<Integer> getMaxBufferSize() {return maxBufferSize;}

    public List<FluidStack> getContents() {
        List<FluidStack> list = new ArrayList<>();

        list.addLast(container.getContent());
        for (FluidStack stack : FluidSlot.stackList(buffer)) {
            list.addLast(stack == null ? FluidStack.EMPTY : stack);
        }
        return list;
    }

    @Override
    public void clearContent() {
        container.clear();
        buffer.clear();
        hasOutputItem = false;
        setIdle();
    }

    public int getRemainingSpace() {
        if (container.getContent().isEmpty()) return getCapacity();
        return getCapacity() - container.getContent().getAmount();
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        int filledAmount = container.fill(resource, action);
        if (filledAmount > 0 && action.execute()) blockEntity.updated();
        return filledAmount;
    }

    public FluidStack drain(FluidStack stack, IFluidHandler.FluidAction action) {
        FluidStack drained = container.drain(stack, action);
        if (action.execute()) blockEntity.updated();
        return drained;
    }

    public FluidStack drain(int amount, IFluidHandler.FluidAction action) {
        FluidStack drained = container.drain(amount, action);
        if (action.execute()) blockEntity.updated();
        return drained;
    }

    public FluidStack drainBufferAt(int index, FluidStack stack, IFluidHandler.FluidAction action) {
        FluidStack drained = buffer.get(index).drain(stack, action);
        if (action.execute()) blockEntity.updated();
        return drained;
    }

    public FluidStack drainBufferAt(int index, int amount, IFluidHandler.FluidAction action) {
        FluidStack drained = buffer.get(index).drain(amount, action);
        if (action.execute()) blockEntity.updated();
        return drained;
    }

    public void dropContents(Level level, BlockPos pos) {
        clearContent();
    }

    @Override
    public void acceptItem(ItemStack stack) {}

    @Override
    public void acceptFluid(FluidStack stack) {
        if (stack.isEmpty()) return;
        FluidUtil.stackOrAppendSlot(buffer, stack, getCapacity());
        if (buffer.size() > maxBufferSize.get()) {
            buffer.subList(maxBufferSize.get(), buffer.size()).clear();
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    protected void flushBuffer() {
        container.setContent(buffer.getFirst().getContent());
        buffer.removeFirst();
        hasOutputItem = true;
        blockEntity.updated();
    }

    protected Optional<RecipeHolder<TrailRecipe>> getCurrentRecipe(ServerLevel level, TrailRecipeInput input) {
        if (level == null || input.isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(LucenticsRecipeTypesRegister.MIXING_TYPE.get(), input, level);
    }

    protected void craft(ServerLevel level, TrailRecipe recipe, TrailRecipeInput input) {
        super.craft(level, recipe, input);
    }

    protected void onCraftStarted(ServerLevel level, BlockPos pos, List<Beam> beams) {}

    protected void whileCrafting(ServerLevel level, BlockPos pos, List<Beam> beams) {}

    protected void onCraftCompleted(ServerLevel level, BlockPos pos, List<Beam> beams) {}

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);

        if (!container.getContent().isEmpty()) {
            FluidStack.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), container.getContent())
                    .result().ifPresent(tag -> nbt.put("container", tag));
        }
        ListTag bufferList = new ListTag();
        for (FluidStack stack : FluidSlot.stackList(buffer)) {
            if (!stack.isEmpty()) bufferList.add(stack.save(registries, new CompoundTag()));
        }
        nbt.put("buffer", bufferList);
        nbt.putInt("processing_continue", processingContinue);
        nbt.putBoolean("has_output_item", hasOutputItem);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);

        container.setContent(nbt.contains("container")
                ? FluidStack.parse(registries, nbt.getCompound("container")).orElse(FluidStack.EMPTY)
                : FluidStack.EMPTY);

        buffer.clear();
        ListTag bufferList = nbt.getList("buffer", net.minecraft.nbt.Tag.TAG_COMPOUND);
        for (int i = 0; i < bufferList.size(); i++) {
            FluidStack.parse(registries, bufferList.getCompound(i)).ifPresent(fluid -> buffer.add(FluidSlot.of(fluid, CAPACITY_BUFFER)));
        }
        processingContinue = nbt.getInt("processing_continue");
        hasOutputItem = nbt.getBoolean("has_output_item");
    }

    @Override
    public boolean isSafeNBT() { return true; }

    protected void syncProgressToClient(ServerLevel level) {}
}
