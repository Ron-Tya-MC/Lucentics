package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import io.github.rontyamc.lucentics.client.particle.GlowParticleOptions;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.ItemUtilities;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class InjectorBehavior extends BlockEntityBehavior implements Clearable {
    public static final BehaviorType<InjectorBehavior> TYPE = new BehaviorType<>("injector");

    private final RandomSource randomSource = RandomSource.create();

    private ItemStack container;
    private ItemStack buffer;
    private boolean hasOutputItem;
    private Supplier<Integer> maxStackSize;
    public InjectorIHandler iHandler;
    private boolean blockMerge;
    private boolean metDayLightCondition;

    private int processingTime = -1;
    private boolean recipeCheck = false;
    private boolean inserted = false;
    private boolean extracted = false;
    private boolean swapped = false;

    public InjectorBehavior(BaseBlockEntity be) {
        super(be);

        hasOutputItem = false;
        metDayLightCondition = true;
        maxStackSize = () -> 64;
        setBlockMerge(true);
        iHandler = new InjectorIHandler(this);
        clearContent();
    }

    public void setBlockMerge(boolean blockMerge) {
        this.blockMerge = blockMerge;
    }

    public boolean getBlockMerge() {
        return blockMerge && !hasOutputItem;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    public ItemStack getContainer() {
        return container == null ? ItemStack.EMPTY : container;
    }

    public ItemStack getBuffer() {
        return buffer == null ? ItemStack.EMPTY : buffer;
    }

    public List<ItemStack> getContents() {
        List<ItemStack> list = new ArrayList<>();

        list.addLast(getContainer());
        list.addLast(getBuffer());
        return list;
    }

    @Override
    public void clearContent() {
        container = ItemStack.EMPTY;
        buffer = ItemStack.EMPTY;
        setIdle();
    }

    public boolean hasOutputItem() {
        return hasOutputItem;
    }

    public boolean metDayLightCondition() {
        return metDayLightCondition;
    }

    public void resetTransportNotifies() {
        inserted = false;
        extracted = false;
        swapped = false;
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getWorld();
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (getContainer().isEmpty()) {
            setIdle();
            metDayLightCondition = true;
            hasOutputItem = false;
            recipeCheck = false;
            if (!buffer.isEmpty()) {
                flushBuffer();
            }
            resetTransportNotifies();
            return;
        }

        swapped = inserted && extracted;
        InjectorRecipeInput input = new InjectorRecipeInput(getContainer());

        if (!isIdle()) {
            if (checkDayLightCondition(serverLevel, input) && !swapped) {
                if (!(processingTime == 1 && getRemainingSpaceBuffer() <= 0)) processingTime--;
                if (processingTime %5 == 0) spawnCraftingParticles(serverLevel);
                blockEntity.setChanged();
                if (processingTime <= 0) {
                    Optional<RecipeHolder<InjectorRecipe>> recipeHolder = getCurrentRecipe(serverLevel, input);
                    if (recipeHolder.isEmpty()) {
                        setIdle();
                    } else {
                        InjectorRecipe recipe = recipeHolder.get().value();
                        craft(serverLevel, recipe, input);
                    }
                }
            } else {
                setIdle();
                recipeCheck = true;
            }
        } else {
            if (inserted) recipeCheck = true;
            if (recipeCheck) {
                if (hasRecipe(serverLevel, input)) {
                    if (checkDayLightCondition(serverLevel, input)) {
                        startProcessing(serverLevel, input);
                    }
                } else {
                    recipeCheck = false;
                }
            }
        }

        resetTransportNotifies();
    }

    private void craft(ServerLevel level, InjectorRecipe recipe, InjectorRecipeInput input) {
        ItemStack result = recipe.getArguments().outputs().getFirst().item().orElse(ItemStack.EMPTY);

        if (recipe.getMainInput().isPresent()) container.shrink(recipe.getMainInput().get().count());

        if (buffer.isEmpty()) {
            buffer = result.copy();
        } else if (getRemainingSpaceBuffer() >= result.getCount()) {
            buffer.grow(result.getCount());
        } else {
            ItemStack stack = result.copyWithCount(result.getCount() - getRemainingSpaceBuffer());
            buffer.grow(getRemainingSpaceBuffer());
            Vec3 vec = getCenter(getPos());
            Containers.dropItemStack(level, vec.x, vec.y, vec.z, stack);
        }

        if (getContainer().isEmpty()) {
            flushBuffer();
            setIdle();
        } else if (hasRecipe(level, input)) {
            startProcessing(level, input);
        } else {
            setIdle();
        }

        spawnCraftCompleteParticles(level);
        blockEntity.updated();
    }

    private void startProcessing(ServerLevel level, InjectorRecipeInput input) {
        processingTime = getCurrentRecipe(level, input)
                .map(r -> r.value().getProcessingDuration())
                .orElse(-1);
        if (processingTime <= 0) {
            processingTime = -1;
        }
    }

    private void flushBuffer() {
        container = buffer;
        buffer = ItemStack.EMPTY;
        hasOutputItem = true;
        blockEntity.updated();
    }

    private void setIdle() {
        processingTime = -1;
    }

    public boolean isIdle() {
        return processingTime == -1;
    }

    private boolean hasRecipe(ServerLevel level, InjectorRecipeInput input) {
        if (getCurrentRecipe(level, input).isEmpty()) {
            metDayLightCondition = true;
            return false;
        }
        else return true;
    }

    private boolean checkDayLightCondition(ServerLevel level, InjectorRecipeInput input) {
        int daylight = getCurrentRecipe(level, input)
                .map(r -> r.value().getDayLightCondition())
                .orElse(0);
        metDayLightCondition = getDaylight(level, getPos()) >= daylight;
        return metDayLightCondition;
    }

    private Optional<RecipeHolder<InjectorRecipe>> getCurrentRecipe(ServerLevel level, InjectorRecipeInput input) {
        if (level == null || input.isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(LucenticsRecipeTypesRegister.INJECTION_TYPE.get(), input, level);
    }

    public static int getDaylight(Level level, BlockPos pos) {
        int i = level.getBrightness(LightLayer.SKY, pos) - level.getSkyDarken();
        float f = level.getSunAngle(1.0F);

        if (i > 0) {
            float f1 = f < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
            f += (f1 - f) * 0.2F;
            i = Math.round((float)i * Mth.cos(f));
        }

        return Mth.clamp(i, 0, 15);
    }

    public void notifyInserted() {
        this.inserted = true;
    }

    public void notifyExtracted() {
        this.extracted = true;
    }

    public int getRemainingSpace() {
        int max = maxStackSize.get();
        if (getContainer().isEmpty()) return max;
        return Math.min(max, getContainer().getMaxStackSize()) - getContainer().getCount();
    }

    public int getRemainingSpaceBuffer() {
        int max = maxStackSize.get();
        if (getBuffer().isEmpty()) return max;
        return Math.min(max, getBuffer().getMaxStackSize()) - getBuffer().getCount();
    }

    public int getSlotLimit() {
        int limit = getContainer().isEmpty() ? 64 : getContainer().getMaxStackSize();
        return Math.min(maxStackSize.get(), limit);
    }

    public int getBufferSlotLimit() {
        int limit = buffer.isEmpty() ? 64 : buffer.getMaxStackSize();
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
            notifyInserted();
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
            notifyExtracted();
            blockEntity.updated();
        }

        return extracted;
    }

    public ItemStack extractBuffer(int amount, boolean simulate) {
        if (buffer.isEmpty()) return ItemStack.EMPTY;

        ItemStack copyStack = buffer.copy();
        ItemStack extracted = copyStack.split(amount);

        if (!simulate) {
            buffer = copyStack;
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

    private void spawnCraftingParticles(ServerLevel level) {
        BlockPos pos = getPos();

        double Sx = pos.getX() + Mth.lerp(randomSource.nextDouble(), 0.1, 0.9);
        double Sy = pos.getY() + 0.9;
        double Sz = pos.getZ() + Mth.lerp(randomSource.nextDouble(), 0.1, 0.9);

        double Tx = pos.getX() + 0.5;
        double Ty = pos.getY() + 0.75;
        double Tz = pos.getZ() + 0.5;

        double Vx = Tx - Sx;
        double Vy = Ty - Sy;
        double Vz = Tz - Sz;

        int rgb = Colors.SUNLIGHT.getColorCode();
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;

        level.sendParticles(new GlowParticleOptions(r,g,b), Sx, Sy, Sz, 0, Vx, Vy, Vz, 0.05);
    }

    private void spawnCraftCompleteParticles(ServerLevel level) {
        BlockPos pos = getPos();

        double Sx = pos.getX() + 0.5;
        double Sy = pos.getY() + 0.75;
        double Sz = pos.getZ() + 0.5;

        level.sendParticles(ParticleTypes.END_ROD, Sx, Sy, Sz, 6, 0.2, 0, 0.2, 0.1);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if (!getContainer().isEmpty()) nbt.put("container", getContainer().save(registries, new CompoundTag()));
        if (!buffer.isEmpty()) nbt.put("buffer", buffer.save(registries, new CompoundTag()));
        nbt.putInt("processing_time", processingTime);
        nbt.putBoolean("has_output_item", hasOutputItem);
        nbt.putBoolean("met_daylight_condition", metDayLightCondition);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        container = nbt.contains("container") ? ItemStack.parse(registries, nbt.getCompound("container")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        buffer = nbt.contains("buffer") ? ItemStack.parse(registries, nbt.getCompound("buffer")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        processingTime = nbt.getInt("processing_time");
        hasOutputItem = nbt.getBoolean("has_output_item");
        metDayLightCondition = nbt.getBoolean("met_daylight_condition");
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }
}
