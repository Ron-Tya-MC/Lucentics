package io.github.rontyamc.lucentics.blocks.milling_table;

import io.github.rontyamc.lucentics.client.particle.GlowParticleOptions;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.util.ItemUtilities;
import io.github.rontyamc.lucentics.common.beam.Beam;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.TrailCraftingBehavior;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipeInput;
import io.github.rontyamc.lucentics.network.MillingProcessPayload;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class MillingTableBehavior extends TrailCraftingBehavior implements Clearable {
    public static final BehaviorType<MillingTableBehavior> TYPE = new BehaviorType<>("milling_table");

    private final RandomSource randomSource = RandomSource.create();

    private ItemStack container = ItemStack.EMPTY;
    private final List<ItemStack> buffer = new ArrayList<>();
    private boolean hasOutputItem;
    private Supplier<Integer> maxStackSize;
    public MillingTableIHandler iHandler;
    private boolean blockMerge;

    public MillingTableBehavior(BaseBlockEntity be) {
        super(be);

        hasOutputItem = false;
        maxStackSize = () -> 64;
        setBlockMerge(true);
        iHandler = new MillingTableIHandler(this);
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

    @Override
    public ItemStack getContainer() {
        return container == null ? ItemStack.EMPTY : container;
    }

    @Override
    public List<ItemStack> getBuffer() {
        return buffer;
    }

    @Override
    public ItemStack getBuffetAt(int index) {
        if (index < 0 || index >= buffer.size()) return ItemStack.EMPTY;
        return buffer.get(index) == null ? ItemStack.EMPTY : buffer.get(index);
    }

    @Override
    public List<ItemStack> collectBuffer() {
        List<ItemStack> collected = new ArrayList<>(buffer);
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

    public List<ItemStack> getContents() {
        List<ItemStack> list = new ArrayList<>();

        list.addLast(getContainer());
        for (ItemStack stack : buffer) {
            list.addLast(stack == null ? ItemStack.EMPTY : stack);
        }
        return list;
    }

    @Override
    public void clearContent() {
        container = ItemStack.EMPTY;
        buffer.clear();
        hasOutputItem = false;
        setIdle();
    }

    public int getRemainingSpace() {
        int max = maxStackSize.get();
        if (getContainer().isEmpty()) return max;
        return Math.min(max, getContainer().getMaxStackSize()) - getContainer().getCount();
    }

    public int getSlotLimit(int slot) {
        int limit;
        if (slot == 0) limit = getContainer().isEmpty() ? 64 : getContainer().getMaxStackSize();
        else limit = getBuffetAt(slot - 1).isEmpty() ? 64 : getBuffetAt(slot - 1).getMaxStackSize();
        return Math.min(maxStackSize.get(), limit);
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!getContainer().isEmpty() && !ItemUtilities.isSameItem(getContainer(), stack, false)) return stack;

        int remainingSpace = getRemainingSpace();
        if (remainingSpace <= 0) return stack;

        int insertCount = Math.min(remainingSpace, stack.getCount());
        ItemStack returnStack = stack.copyWithCount(stack.getCount() - insertCount);

        if (!simulate) {
            if (getContainer().isEmpty()) {
                container = stack.copyWithCount(insertCount);
            } else {
                container.grow(insertCount);
            }
            hasOutputItem = false;
            blockEntity.updated();
        }

        return returnStack;
    }

    public ItemStack extract(int amount, boolean simulate) {
        if (getContainer().isEmpty()) return ItemStack.EMPTY;

        ItemStack copyStack = getContainer().copy();
        ItemStack extracted = copyStack.split(amount);

        if (!simulate) {
            container = copyStack;
            if (container.isEmpty()) hasOutputItem = false;
            blockEntity.updated();
        }

        return extracted;
    }

    public ItemStack extractBufferAt(int index, int amount, boolean simulate) {
        if (getBuffetAt(index).isEmpty()) return ItemStack.EMPTY;

        ItemStack copyStack = getBuffetAt(index).copy();
        ItemStack extracted = copyStack.split(amount);

        if (!simulate) {
            if (copyStack.isEmpty()) {
                buffer.remove(index);
            } else {
                buffer.set(index, copyStack);
            }
            blockEntity.updated();
        }

        return extracted;
    }

    public void dropContents(Level level, BlockPos pos) {
        Vec3 vec = getCenter(pos);
        List<ItemStack> contents = getContents();

        if (!contents.isEmpty()) {
            for (ItemStack stack : contents) {
                Containers.dropItemStack(level, vec.x, vec.y, vec.z, stack);
            }
        }
        clearContent();
    }

    private void spawnCraftingParticles(ServerLevel level, List<Beam> beams, BlockPos pos) {
        double Sx = pos.getX() + Mth.lerp(randomSource.nextDouble(), 3.0f / 8, 5.0f / 8);
        double Sy = pos.getY() + Mth.lerp(randomSource.nextDouble(), 3.0f / 8, 1.0);
        double Sz = pos.getZ() + Mth.lerp(randomSource.nextDouble(), 3.0f / 8, 5.0f / 8);

        double Tx = Sx + Mth.lerp(randomSource.nextDouble(), -0.2, 0.2);
        double Ty = pos.getY() + 1.2;
        double Tz = Sz + Mth.lerp(randomSource.nextDouble(), -0.2, 0.2);

        double Vx = Tx - Sx;
        double Vy = Ty - Sy;
        double Vz = Tz - Sz;

        int rgb = Colors.RED.getColorCode();
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
    public void tick() {
        processingContinueMax = processingTimeMax * 2;
        super.tick();
    }

    protected void flushBuffer() {
        container = buffer.getFirst();
        buffer.removeFirst();
        hasOutputItem = true;
        blockEntity.updated();
    }

    protected Optional<RecipeHolder<TrailRecipe>> getCurrentRecipe(ServerLevel level, TrailRecipeInput input) {
        if (level == null || input.isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(LucenticsRecipeTypesRegister.MILLING_TYPE.get(), input, level);
    }

    protected void craft(ServerLevel level, TrailRecipe recipe, TrailRecipeInput input) {
        super.craft(level, recipe, input);
    }

    @Override
    protected void onCraftStarted(ServerLevel level, BlockPos pos, List<Beam> beams) {}

    @Override
    protected void whileCrafting(ServerLevel level, BlockPos pos, List<Beam> beams) {
        if (processingTime %7 == 0) {
            spawnCraftingParticles(level, beams, pos);
            spawnCraftingParticles(level, beams, pos);
        }
        if (processingTime %2 == 0) {
            double Sx = pos.getX() + 0.5;
            double Sy = pos.getY() + 0.5;
            double Sz = pos.getZ() + 0.5;

            level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, container), Sx, Sy, Sz, 2, 0.3, 0.1, 0.3, 0.01);
        }
        if (processingTime %2 == 0) {
            float p = Mth.lerp(randomSource.nextFloat(), 0.1f, 0.3f);
            level.playSound(null, pos, SoundEvents.TUFF_BREAK, SoundSource.BLOCKS, 0.4f, p);
        }
    }

    @Override
    protected void onCraftCompleted(ServerLevel level, BlockPos pos, List<Beam> beams) {
        spawnCraftCompleteParticles(level);

        if (randomSource.nextFloat() < 0.5f) level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.4f, 0.5f);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);

        if (!getContainer().isEmpty()) {
            nbt.put("container", getContainer().save(registries, new CompoundTag()));
        }
        ListTag bufferList = new ListTag();
        for (ItemStack stack : buffer) {
            if (!stack.isEmpty()) bufferList.add(stack.save(registries, new CompoundTag()));
        }
        nbt.put("buffer", bufferList);
        nbt.putInt("processing_continue", processingContinue);
        nbt.putBoolean("has_output_item", hasOutputItem);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);

        container = nbt.contains("container")
                ? ItemStack.parse(registries, nbt.getCompound("container")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;

        buffer.clear();
        ListTag bufferList = nbt.getList("buffer", net.minecraft.nbt.Tag.TAG_COMPOUND);
        for (int i = 0; i < bufferList.size(); i++) {
            ItemStack.parse(registries, bufferList.getCompound(i)).ifPresent(buffer::add);
        }
        processingContinue = nbt.getInt("processing_continue");
        hasOutputItem = nbt.getBoolean("has_output_item");
    }

    @Override
    public boolean isSafeNBT() { return true; }

    private int processingContinueClient = 0;
    private int processingTimeMaxClient = -1;

    public void applyClientProgress(int processingContinue, int processingTimeMax) {
        this.processingContinueClient = processingContinue;
        this.processingTimeMaxClient = processingTimeMax;
    }

    public int getProcessingContinueClient() { return processingContinueClient; }

    public int getProcessingTimeMaxClient() { return processingTimeMaxClient; }

    protected void syncProgressToClient(ServerLevel level) {
        var payload = new MillingProcessPayload(getPos(), processingContinue, processingTimeMax);
        PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(getPos()), payload);
    }
}
