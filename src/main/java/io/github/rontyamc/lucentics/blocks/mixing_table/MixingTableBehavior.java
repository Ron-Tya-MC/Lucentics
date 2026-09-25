package io.github.rontyamc.lucentics.blocks.mixing_table;

import io.github.rontyamc.lucentics.client.particle.GlowParticleOptions;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.FluidSlot;
import io.github.rontyamc.lucentics.common.ThingStack;
import io.github.rontyamc.lucentics.common.beam.Beam;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.TrailCraftingBehavior;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.util.FluidUtil;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipeInput;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
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
    public static final BehaviorType<MixingTableBehavior> TYPE = new BehaviorType<>("mixing_table", TrailCraftingBehavior.TYPE);

    public static final int CAPACITY_CONTAINER = 8 * FluidType.BUCKET_VOLUME;
    public static final int CAPACITY_BUFFER = 4 * FluidType.BUCKET_VOLUME;

    private final FluidSlot container;
    private List<FluidSlot> buffer = new ArrayList<>();
    private boolean hasOutputItem;
    private final Supplier<Integer> maxBufferSize;
    private final MixingTableFHandler fHandler;
    private final MixingTableFHandler adminFHandler;
    private boolean blockMerge;

    public MixingTableBehavior(BaseBlockEntity be) {
        super(be);

        hasOutputItem = false;
        this.container = new FluidSlot(CAPACITY_CONTAINER);
        maxBufferSize = () -> 1;
        setBlockMerge(true);
        fHandler = new MixingTableFHandler(this);
        adminFHandler = new MixingTableFHandler(this, true);
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

    public int getCapacityContainer() {
        return CAPACITY_CONTAINER;
    }

    public int getCapacityBuffer() {
        return CAPACITY_BUFFER;
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

    public MixingTableFHandler getFHandler() {
        return fHandler;
    }

    public MixingTableFHandler getAdminFHandler() {
        return adminFHandler;
    }

    @Override
    public void clearContent() {
        container.clear();
        buffer.clear();
        hasOutputItem = false;
        setIdle();
    }

    public int getRemainingSpace() {
        if (container.getContent().isEmpty()) return getCapacityContainer();
        return getCapacityContainer() - container.getContent().getAmount();
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
        if (getBufferAt(index).isEmpty()) return FluidStack.EMPTY;

        FluidStack drained = buffer.get(index).drain(stack, action);
        if (action.execute()) {
            if (buffer.get(index).getContent().isEmpty()) buffer.remove(index);
            blockEntity.updated();
        }
        return drained;
    }

    public FluidStack drainBufferAt(int index, int amount, IFluidHandler.FluidAction action) {
        if (getBufferAt(index).isEmpty()) return FluidStack.EMPTY;

        FluidStack drained = buffer.get(index).drain(amount, action);
        if (action.execute())  {
            if (buffer.get(index).getContent().isEmpty()) buffer.remove(index);
            blockEntity.updated();
        }
        return drained;
    }

    public void dropContents(Level level, BlockPos pos) {
        clearContent();
    }

    @Override
    public FluidStack acceptFluid(FluidStack stack, boolean simulate) {
        if (stack.isEmpty()) return FluidStack.EMPTY;

        List<FluidSlot> newBuffer = FluidUtil.stackOrAppend(buffer, stack, getCapacityBuffer());
        if (!simulate) buffer = newBuffer;

        if (newBuffer.size() > maxBufferSize.get()) {
            FluidStack leftover = newBuffer.getLast().getContent();
            if (!simulate) buffer.removeLast();

            return leftover;
        }
        return FluidStack.EMPTY;
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

    protected boolean craft(ServerLevel level, RecipeHolder<TrailRecipe> recipeHolder, TrailRecipeInput input) {
        boolean craftSucceeded = super.craft(level, recipeHolder, input);

        if (craftSucceeded) {
            Optional<List<TrailRecipe.ConsumptionEntry>> catalysts = getCachedCatalysts();
            if (catalysts.isPresent()) {
                for (TrailRecipe.ConsumptionEntry entry : catalysts.get()) {
                    entry.device().catalyst();
                }
            }
        }

        return craftSucceeded;
    }

    protected void onCraftStarted(ServerLevel level, BlockPos pos, List<Beam> beams) {
        level.playSound(null, pos, SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.BLOCKS, 0.4f, 2.0f);
    }

    protected void whileCrafting(ServerLevel level, BlockPos pos, List<Beam> beams) {
        if (processingTime %7 == 0) {
            spawnCraftingParticles(level, beams, pos);
            spawnCraftingParticles(level, beams, pos);
        }
        if (processingTime %2 == 0) {
            float p = Mth.lerp(RANDOM_SOURCE.nextFloat(), 0.1f, 0.3f);
            level.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.4f, p);
        }
    }

    protected void onCraftCompleted(ServerLevel level, BlockPos pos, List<Beam> beams) {
        spawnCraftCompleteParticles(level);

        level.playSound(null, pos, SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.BLOCKS, 0.4f, 2.0f);
    }

    private void spawnCraftingParticles(ServerLevel level, List<Beam> beams, BlockPos pos) {
        double Sx = pos.getX() + Mth.lerp(RANDOM_SOURCE.nextDouble(), 3.0f / 8, 5.0f / 8);
        double Sy = pos.getY() + Mth.lerp(RANDOM_SOURCE.nextDouble(), 3.0f / 8, 1.0);
        double Sz = pos.getZ() + Mth.lerp(RANDOM_SOURCE.nextDouble(), 3.0f / 8, 5.0f / 8);

        double Tx = Sx + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);
        double Ty = pos.getY() + 1.2;
        double Tz = Sz + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);

        double Vx = Tx - Sx;
        double Vy = Ty - Sy;
        double Vz = Tz - Sz;

        int rgb = Colors.BLUE.getColorCode();
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;

        level.sendParticles(new GlowParticleOptions(r,g,b), Sx, Sy, Sz, 0, Vx, Vy, Vz, 0.15);
    }

    private void spawnCraftCompleteParticles(ServerLevel level) {
        BlockPos pos = getPos();

        double Sx = pos.getX() + 0.5;
        double Sy = pos.getY() + 0.8;
        double Sz = pos.getZ() + 0.5;

        level.sendParticles(ParticleTypes.END_ROD, Sx, Sy, Sz, 2, 0.3, 0, 0.3, 0.1);
    }

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
            FluidStack.parse(registries, bufferList.getCompound(i)).ifPresent(fluid -> buffer.add(FluidSlot.of(fluid, getCapacityBuffer())));
        }

        processingContinue = nbt.getInt("processing_continue");
        hasOutputItem = nbt.getBoolean("has_output_item");
    }

    @Override
    public boolean isSafeNBT() { return true; }

    protected void syncProgressToClient(ServerLevel level) {}
}
