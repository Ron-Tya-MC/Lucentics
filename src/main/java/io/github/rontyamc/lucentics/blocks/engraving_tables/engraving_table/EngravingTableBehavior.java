package io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table;

import io.github.rontyamc.lucentics.client.particle.GlowParticleOptions;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.ThingStack;
import io.github.rontyamc.lucentics.common.beam.Beam;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.TrailCraftingBehavior;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipeInput;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class EngravingTableBehavior extends TrailCraftingBehavior implements Clearable {
    public static final BehaviorType<EngravingTableBehavior> TYPE = new BehaviorType<>("engraving_table");

    private final RandomSource randomSource = RandomSource.create();

    private ItemStack container;
    private final List<ItemStack> buffer = new ArrayList<>();
    private boolean hasOutputItem;
    private final Integer maxStackSize;
    private final Supplier<Integer> maxBufferSize;
    public EngravingTableIHandler iHandler;
    private boolean blockMerge;

    public EngravingTableBehavior(BaseBlockEntity be) {
        super(be);

        hasOutputItem = false;
        maxStackSize = 64;
        maxBufferSize = () -> 6;
        setBlockMerge(true);
        iHandler = new EngravingTableIHandler(this);
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
    public ThingStack getContainer(){
        return ThingStack.of(container);
    }

    @Override
    public void setContainer(ThingStack stack) {
        container = stack.asItemOrEmpty();
    }

    public List<ThingStack> getBuffer() {
        return ThingStack.fromItems(buffer);
    }

    public ItemStack getBufferAt(int index) {
        if (index < 0 || index >= buffer.size()) return ItemStack.EMPTY;
        return buffer.get(index) == null ? ItemStack.EMPTY : buffer.get(index);
    }

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

    public Integer getMaxStackSize() {return maxStackSize;}

    public Supplier<Integer> getMaxBufferSize() {return maxBufferSize;}

    public List<ItemStack> getContents() {
        List<ItemStack> list = new ArrayList<>();

        list.addLast(getContainer().asItemOrEmpty());
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
        int max = maxStackSize;
        if (getContainer().isEmpty()) return max;
        return Math.min(max, getContainer().asItemOrEmpty().getMaxStackSize()) - getContainer().asItemOrEmpty().getCount();
    }

    public int getSlotLimit(int slot) {
        int limit;
        if (slot == 0) limit = getContainer().isEmpty() ? 64 : getContainer().asItemOrEmpty().getMaxStackSize();
        else limit = getBufferAt(slot - 1).isEmpty() ? 64 : getBufferAt(slot - 1).getMaxStackSize();
        return Math.min(maxStackSize, limit);
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!getContainer().isEmpty() && !ItemUtil.isSameItem(getContainer().asItemOrEmpty(), stack, false)) return stack;

        int remainingSpace = getRemainingSpace();
        if (remainingSpace <= 0) return stack;

        int insertCount = Math.min(remainingSpace, stack.getCount());
        ItemStack leftover = stack.copyWithCount(stack.getCount() - insertCount);

        if (!simulate) {
            if (getContainer().isEmpty()) {
                container = stack.copyWithCount(insertCount);
            } else {
                container.grow(insertCount);
            }
            hasOutputItem = false;
            blockEntity.updated();
        }

        return leftover;
    }

    public ItemStack extract(int amount, boolean simulate) {
        if (getContainer().isEmpty()) return ItemStack.EMPTY;

        ItemStack copyStack = getContainer().asItemOrEmpty().copy();
        ItemStack extracted = copyStack.split(amount);

        if (!simulate) {
            container = copyStack;
            if (container.isEmpty()) hasOutputItem = false;
            blockEntity.updated();
        }

        return extracted;
    }

    public ItemStack extractBufferAt(int index, int amount, boolean simulate) {
        if (getBufferAt(index).isEmpty()) return ItemStack.EMPTY;

        ItemStack copyStack = getBufferAt(index).copy();
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

    @Override
    public void acceptItem(ItemStack stack) {
        if (stack.isEmpty()) return;
        ItemUtil.stackOrAppend(buffer, stack);
        if (buffer.size() > maxBufferSize.get()) {
            List<ItemStack> leftovers = new ArrayList<>(buffer.subList(maxBufferSize.get(), buffer.size()));
            buffer.subList(maxBufferSize.get(), buffer.size()).clear();
            ItemUtil.dropItem(getWorld(), getPos(), leftovers);
        }
    }

    @Override
    public void acceptFluid(FluidStack stack) {}

    public void dropContents(Level level, BlockPos pos) {
        ItemUtil.dropItem(level, pos, getContents());
        clearContent();
    }

    private void spawnCraftingParticles(ServerLevel level, BlockPos pos, List<Beam> beams) {
        double dx = randomSource.nextDouble() < 0.5 ? 3.0d / 32 : 29.0d / 32;
        double dy = Mth.lerp(randomSource.nextDouble(), 0.1, 0.5);
        double dz = randomSource.nextDouble() < 0.5 ? 3.0d / 32 : 29.0d / 32;

        double Sx = pos.getX() + dx;
        double Sy = pos.getY() + 7.0d / 8;
        double Sz = pos.getZ() + dz;

        double Tx = Sx + Mth.lerp(randomSource.nextDouble(), -0.2, 0.2);
        double Ty = Sy + dy;
        double Tz = Sz + Mth.lerp(randomSource.nextDouble(), -0.2, 0.2);

        double Vx = Tx - Sx;
        double Vy = Ty - Sy;
        double Vz = Tz - Sz;

        int rgb;

        if (!beams.isEmpty()) {
            List<Integer> colors = new ArrayList<>();
            for (Beam beam : beams) {
                colors.add(beam.color().getColorCode());
            }
            rgb = colors.get(Mth.lerpInt(randomSource.nextFloat(), 1, colors.size()) - 1);
        } else {
            rgb = Colors.SUNLIGHT.getColorCode();
        }
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;

        level.sendParticles(new GlowParticleOptions(r,g,b), Sx, Sy, Sz, 0, Vx, Vy, Vz, 0.07);
    }

    private void spawnCraftCompleteParticles(ServerLevel level, BlockPos pos) {
        double Sx = pos.getX() + 0.5;
        double Sy = pos.getY() + 0.8;
        double Sz = pos.getZ() + 0.5;

        level.sendParticles(ParticleTypes.END_ROD, Sx, Sy, Sz, 9, 0.3, 0, 0.3, 0.1);
    }

    @Override
    public void tick() {
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
        return level.getRecipeManager().getRecipeFor(LucenticsRecipeTypesRegister.ENGRAVING_TYPE.get(), input, level);
    }

    protected void craft(ServerLevel level, TrailRecipe recipe, TrailRecipeInput input) {
        super.craft(level, recipe, input);

        Optional<List<TrailRecipe.ConsumptionEntry>> catalysts = getCachedCatalysts();
        if (catalysts.isPresent()) {
            for (TrailRecipe.ConsumptionEntry entry : catalysts.get()) {
                entry.device().catalyst();
            }
        }
    }

    protected void onCraftStarted(ServerLevel level, BlockPos pos, List<Beam> beams) {}

    protected void whileCrafting(ServerLevel level, BlockPos pos, List<Beam> beams) {
        if (processingTime %3 == 0) {
            spawnCraftingParticles(level, pos, beams);
            spawnCraftingParticles(level, pos, beams);
        }
        if (processingTime %7 == processingTimeMax %7) {
            level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.4f, 1.5f);
        }
    }

    protected void onCraftCompleted(ServerLevel level, BlockPos pos, List<Beam> beams) {
        spawnCraftCompleteParticles(level, pos);
        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.4f, 1.5f);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);

        if (!getContainer().isEmpty()) {
            nbt.put("container", getContainer().asItemOrEmpty().save(registries, new CompoundTag()));
        }
        ListTag bufferList = new ListTag();
        for (ItemStack stack : buffer) {
            if (!stack.isEmpty()) bufferList.add(stack.save(registries, new CompoundTag()));
        }
        nbt.put("buffer", bufferList);
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

        hasOutputItem = nbt.getBoolean("has_output_item");
    }

    @Override
    public boolean isSafeNBT() { return true; }

    protected void syncProgressToClient(ServerLevel level) {}
}
