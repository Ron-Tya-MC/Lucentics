package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import io.github.rontyamc.lucentics.client.particle.GlowParticleOptions;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.recipe.OutputReceiver;
import io.github.rontyamc.lucentics.common.recipe.OutputRoller;
import io.github.rontyamc.lucentics.common.util.ItemUtilities;
import io.github.rontyamc.lucentics.recipes.injecting.InjectingRecipe;
import io.github.rontyamc.lucentics.recipes.injecting.InjectingRecipeInput;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InjectorBehavior extends BlockEntityBehavior implements Clearable, OutputReceiver {
    public static final BehaviorType<InjectorBehavior> TYPE = new BehaviorType<>("injector");

    private final RandomSource randomSource = RandomSource.create();

    private ItemStack container;
    private ItemStack buffer;
    private boolean hasOutputItem;
    private final Integer maxStackSize;
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
        maxStackSize = 64;
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

    public void setHasOutputItem(boolean hasOutputItem) {
        this.hasOutputItem = hasOutputItem;
    }
    
    public Integer getMaxStackSize() {return maxStackSize;}

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
        InjectingRecipeInput input = new InjectingRecipeInput(getContainer());

        if (!isIdle()) {
            Optional<RecipeHolder<InjectingRecipe>> recipeHolder = getCurrentRecipe(serverLevel, input);
            if (recipeHolder.isPresent() && checkDayLightCondition(serverLevel, recipeHolder.get().value()) && !swapped) {
                if (!(processingTime == 1 && getRemainingSpaceBuffer() <= 0)) processingTime--;
                whileCrafting(serverLevel, getPos());
                blockEntity.setChanged();
                if (processingTime <= 0) {
                    craft(serverLevel, recipeHolder.get().value(), input);
                }
            } else {
                setIdle();
                recipeCheck = true;
            }
        } else {
            if (inserted) recipeCheck = true;
            if (recipeCheck) {
                Optional<RecipeHolder<InjectingRecipe>> recipeHolder = getCurrentRecipe(serverLevel, input);
                if (recipeHolder.isPresent()) {
                    InjectingRecipe recipe = recipeHolder.get().value();
                    if (checkDayLightCondition(serverLevel, recipe)) {
                        startProcessing(recipe);
                    }
                } else {
                    recipeCheck = false;
                }
            }
        }

        resetTransportNotifies();
    }

    private void craft(ServerLevel level, InjectingRecipe recipe, InjectingRecipeInput input) {
        if (recipe.getMainInput().isPresent()) container.shrink(recipe.getMainInput().get().count());

        for (OutputRoller.RolledOutput rolled : OutputRoller.roll(randomSource, recipe.getOutputs())) {
            rolled.item().ifPresent(this::acceptItem);
            rolled.fluid().ifPresent(this::acceptFluid);
        }

        if (getContainer().isEmpty()) {
            flushBuffer();
            setIdle();
        } else {
            startProcessing(level, new InjectingRecipeInput(getContainer()));
        }

        onCraftCompleted(level, getPos());
        blockEntity.updated();
    }

    protected void onCraftStarted(ServerLevel level, BlockPos pos) {}

    protected void whileCrafting(ServerLevel level, BlockPos pos) {
        if (processingTime %5 == 0) spawnCraftingParticles(level, pos);
    }

    protected void onCraftCompleted(ServerLevel level, BlockPos pos) {
        spawnCraftCompleteParticles(level, pos);
        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.4f, 1.5f);
    }

    private void startProcessing(ServerLevel level, InjectingRecipeInput input) {
        getCurrentRecipe(level, input)
                .ifPresentOrElse(recipe -> startProcessing(recipe.value()),
                        this::setIdle);
    }

    private void startProcessing(InjectingRecipe recipe) {
        processingTime = recipe.getProcessingDuration();
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

    private boolean checkDayLightCondition(ServerLevel level, InjectingRecipe recipe) {
        metDayLightCondition = getDaylight(level, getPos()) >= recipe.getDayLightCondition();
        return metDayLightCondition;
    }

    private Optional<RecipeHolder<InjectingRecipe>> getCurrentRecipe(ServerLevel level, InjectingRecipeInput input) {
        if (level == null || input.isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(LucenticsRecipeTypesRegister.INJECTING_TYPE.get(), input, level);
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
        int max = maxStackSize;
        if (getContainer().isEmpty()) return max;
        return Math.min(max, getContainer().getMaxStackSize()) - getContainer().getCount();
    }

    public int getRemainingSpaceBuffer() {
        int max = maxStackSize;
        if (getBuffer().isEmpty()) return max;
        return Math.min(max, getBuffer().getMaxStackSize()) - getBuffer().getCount();
    }

    public int getSlotLimit() {
        int limit = getContainer().isEmpty() ? 64 : getContainer().getMaxStackSize();
        return Math.min(maxStackSize, limit);
    }

    public int getBufferSlotLimit() {
        int limit = buffer.isEmpty() ? 64 : buffer.getMaxStackSize();
        return Math.min(maxStackSize, limit);
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!getContainer().isEmpty() && !ItemUtilities.isSameItem(getContainer(), stack, false)) return stack;

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
            notifyInserted();
            blockEntity.updated();
        }

        return leftover;
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

    @Override
    public void acceptItem(ItemStack stack) {
        if (stack.isEmpty()) return;

        ItemStack leftover = stack.copy();

        if (buffer.isEmpty()) {
            buffer = leftover.split(Math.min(stack.getMaxStackSize(), maxStackSize));
        } else if (ItemUtilities.isSameItem(buffer, stack, false)) {
            int insertCount = Math.min(getRemainingSpaceBuffer(), stack.getCount());
            leftover = stack.copyWithCount(buffer.getCount() - insertCount);
            if (insertCount > 0) buffer.grow(insertCount);
        }
        ItemUtilities.dropItem(getWorld(), getPos(), leftover);
    }

    @Override
    public void acceptFluid(FluidStack stack) {}

    public void dropContents(Level level, BlockPos pos) {
        ItemUtilities.dropItem(level, pos, getContents());
        clearContent();
    }

    private void spawnCraftingParticles(ServerLevel level, BlockPos pos) {
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

    private void spawnCraftCompleteParticles(ServerLevel level, BlockPos pos) {
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
