package io.github.rontyamc.lucentics.common.behavior;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.ThingStack;
import io.github.rontyamc.lucentics.common.beam.Beam;
import io.github.rontyamc.lucentics.common.recipe.OutputReceiver;
import io.github.rontyamc.lucentics.common.recipe.OutputRoller;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBehavior.getDaylight;

public abstract class TrailCraftingBehavior extends ReceiveBehavior implements OutputReceiver {
    protected int processingTime = -1;
    protected int processingTimeMax = -1;
    protected int processingContinue = 0;
    protected int processingContinueMax = 0;
    protected boolean metDayLightCondition;

    private Optional<List<TrailRecipe.ConsumptionEntry>> cachedCatalysts = Optional.empty();

    public TrailCraftingBehavior(BaseBlockEntity be) {
        super(be);

        metDayLightCondition = true;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public int getProcessingTimeMax() {
        return processingTimeMax;
    }

    public int getProcessingContinue() {
        return processingContinue;
    }

    public int getProcessingContinueMax() {
        return processingContinueMax;
    }

    public boolean metDayLightCondition() {
        return metDayLightCondition;
    }

    public abstract boolean hasOutputItem();

    public abstract void setHasOutputItem(boolean hasOutputItem);

    public Optional<List<TrailRecipe.ConsumptionEntry>> getCachedCatalysts() {
        return cachedCatalysts;
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getWorld();
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (isIdle() && processingContinue >= 1) {
            processingContinue -= 1;
            syncProgressToClient(serverLevel);
        }

        List<Beam> beams = List.copyOf(getTrails());
        trails.clear();
        BlockPos pos = getPos();

        if (getContainer().isEmpty()) {
            setIdle();
            setHasOutputItem(false);
            metDayLightCondition = true;
            if (!getBuffer().isEmpty()) {
                flushBuffer();
            }
            return;
        }

        TrailRecipeInput input = TrailRecipeInput.of(getContainer(), beams);
        Optional<RecipeHolder<TrailRecipe>> recipeHolder = getCurrentRecipe(serverLevel, input);
        if (recipeHolder.isEmpty()) {
            metDayLightCondition = true;
            setIdle();
            return;
        }

        TrailRecipe recipe = recipeHolder.get().value();

        if (!checkDayLightCondition(serverLevel, recipe)) {
            setIdle();
            return;
        }
        if (isIdle()) {
            startProcessing(serverLevel, recipe, beams);
            return;
        }

        processingTime--;
        if (processingContinue < processingContinueMax) processingContinue++;
        syncProgressToClient(serverLevel);
        whileCrafting(serverLevel, pos, beams);
        if (processingTime <= 0) {
            craft(serverLevel, recipe, input);
        }
    }

    protected abstract ThingStack getContainer();

    protected abstract void setContainer(ThingStack stack);

    protected abstract List<ThingStack> getBuffer();

    protected abstract void flushBuffer();

    protected boolean checkDayLightCondition(ServerLevel level, TrailRecipe recipe) {
        metDayLightCondition = getDaylight(level, getPos()) >= recipe.getDayLightCondition();
        return metDayLightCondition;
    }

    protected void setIdle() {
        processingTime = -1;
    }

    public boolean isIdle() {
        return processingTime == -1;
    }

    protected abstract Optional<RecipeHolder<TrailRecipe>> getCurrentRecipe(ServerLevel level, TrailRecipeInput input);

    protected void craft(ServerLevel level, TrailRecipe recipe, TrailRecipeInput input) {
        Optional<List<TrailRecipe.ConsumptionEntry>> consumption = recipe.resolveConsumptionCached(input, level);
        if (consumption.isEmpty()) return;

        setContainer(getContainer().shrunken(recipe.getMainInput().amount()));

        List<TrailRecipe.ConsumptionEntry> catalysts = new ArrayList<>();
        for (TrailRecipe.ConsumptionEntry entry : consumption.get()) {
            if (entry.notConsume()) {
                catalysts.add(entry);
            } else {
                entry.device().consume(entry.amount());
            }
        }

        for (OutputRoller.RolledOutput rolled : OutputRoller.roll(level.getRandom(), recipe.getOutputs())) {
            rolled.item().ifPresent(this::acceptItem);
            rolled.fluid().ifPresent(this::acceptFluid);
        }

        if (getContainer().isEmpty()) {
            flushBuffer();
            setIdle();
        } else {
            startProcessing(level, new TrailRecipeInput(getContainer(), input.beams()));
        }

        cachedCatalysts = Optional.of(catalysts);
        onCraftCompleted(level, getPos(), input.beams());
        blockEntity.updated();
    }

    protected void startProcessing(ServerLevel level, TrailRecipeInput input) {
        getCurrentRecipe(level, input)
                .ifPresentOrElse(recipe -> startProcessing(level, recipe.value(), input.beams()),
                        this::setIdle);
    }

    protected void startProcessing(ServerLevel level, TrailRecipe recipe, List<Beam> beams) {
        processingTime = recipe.getProcessingDuration();
        if (processingTime <= 0) {
            processingTime = -1;
        }
        processingTimeMax = processingTime;
        onCraftStarted(level, getPos(), beams);
    }

    protected abstract void onCraftStarted(ServerLevel level, BlockPos pos, List<Beam> beams);

    protected abstract void whileCrafting(ServerLevel level, BlockPos pos, List<Beam> beams);

    protected abstract void onCraftCompleted(ServerLevel level, BlockPos pos, List<Beam> beams);

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putInt("processing_time", processingTime);
        nbt.putBoolean("met_daylight_condition", metDayLightCondition);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        processingTime = nbt.getInt("processing_time");
        metDayLightCondition = nbt.getBoolean("met_daylight_condition");
    }

    protected abstract void syncProgressToClient(ServerLevel level);
}
