package io.github.rontyamc.lucentics.common.behavior;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.PendingOutputs;
import io.github.rontyamc.lucentics.common.ThingStack;
import io.github.rontyamc.lucentics.common.beam.Beam;
import io.github.rontyamc.lucentics.common.recipe.OutputReceiver;
import io.github.rontyamc.lucentics.common.recipe.OutputRoller;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.rontyamc.lucentics.blocks.injector.InjectorBehavior.getDaylight;

public abstract class TrailCraftingBehavior extends ReceiveBehavior implements OutputReceiver {
    protected int processingTime = -1;
    protected int processingTimeMax = -1;
    protected int processingContinue = 0;
    protected int processingContinueMax = 0;
    protected boolean metDayLightCondition;

    protected PendingOutputs pendingOutputs = PendingOutputs.empty();

    protected static final RandomSource RANDOM_SOURCE = RandomSource.create();

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

    public void resetProcessingContinue(ServerLevel level) {
        processingContinue = 0;
        syncProgressToClient(level);
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

        if ((isIdle() || isPending()) && processingContinue >= 1) {
            processingContinue -= 1;
            syncProgressToClient(serverLevel);
        }

        List<Beam> beams = List.copyOf(getTrails());
        trails.clear();
        BlockPos pos = getPos();

        if (getContainer().isEmpty()) {
            resetStates();
            if (!getBuffer().isEmpty()) {
                flushBuffer();
            }
            return;
        }

        if (hasOutputItem()) {
            setIdle();
            metDayLightCondition = true;
            return;
        }

        TrailRecipeInput input = TrailRecipeInput.of(getContainer(), beams);
        Optional<RecipeHolder<TrailRecipe>> recipeHolder = getCurrentRecipe(serverLevel, input);
        if (recipeHolder.isEmpty()) {
            resetStates();
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

        else if (!pendingOutputs.isEmpty()) {
            if (pendingOutputs.recipeId().equals(recipeHolder.get().id())) {
                whilePending(serverLevel, pos, beams);

                boolean inserted = true;

                for (ThingStack output : pendingOutputs.contents()) {
                    if (output.isItem()) {
                        ItemStack leftover = acceptItem(output.asItemOrEmpty(), true);
                        if (!leftover.isEmpty()) {
                            inserted = false;
                            break;
                        }
                    }
                    if (output.isFluid()) {
                        FluidStack leftover = acceptFluid(output.asFluidOrEmpty(), true);
                        if (!leftover.isEmpty()) {
                            inserted = false;
                            break;
                        }
                    }
                }

                if (inserted) {
                    List<OutputRoller.RolledOutput> outputs = new ArrayList<>();
                    for (ThingStack stack : pendingOutputs.contents()) {
                        outputs.add(OutputRoller.RolledOutput.of(stack));
                    }

                    pendingOutputs.clear();
                    craftWithRolledOutputs(serverLevel, recipeHolder.get(), outputs, input, false);
                }
            }
            else {
                pendingOutputs.clear();
            }
            return;
        }

        processingTime--;
        if (processingContinue < processingContinueMax) processingContinue++;
        syncProgressToClient(serverLevel);
        whileCrafting(serverLevel, pos, beams);
        if (processingTime <= 0) {
            craft(serverLevel, recipeHolder.get(), input);
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
        pendingOutputs.clear();
    }

    public boolean isIdle() {
        return processingTime == -1;
    }

    public boolean isPending() {
        return !pendingOutputs.isEmpty();
    }

    public void resetStates() {
        setIdle();
        setHasOutputItem(false);
        metDayLightCondition = true;
    }

    protected abstract Optional<RecipeHolder<TrailRecipe>> getCurrentRecipe(ServerLevel level, TrailRecipeInput input);

    protected boolean craft(ServerLevel level, RecipeHolder<TrailRecipe> recipeHolder, TrailRecipeInput input) {
        TrailRecipe recipe = recipeHolder.value();

        List<OutputRoller.RolledOutput> rolled = OutputRoller.roll(RANDOM_SOURCE, recipe.getOutputs());

        return craftWithRolledOutputs(level, recipeHolder, rolled, input, true);
    }

    private boolean craftWithRolledOutputs(ServerLevel level, RecipeHolder<TrailRecipe> recipeHolder, List<OutputRoller.RolledOutput> rolled, TrailRecipeInput input, boolean checkAcceptable) {
        TrailRecipe recipe = recipeHolder.value();

        Optional<List<TrailRecipe.ConsumptionEntry>> consumption = recipe.resolveConsumption(input, level);
        if (consumption.isEmpty()) return false;

        List<TrailRecipe.ConsumptionEntry> catalysts = new ArrayList<>();

        boolean craftSucceeded = true;

        if (checkAcceptable) {
            for (OutputRoller.RolledOutput output : rolled) {
                if (output.item().isPresent()) {
                    ItemStack leftover = acceptItem(output.item().get(), true);
                    if (!leftover.isEmpty()) {
                        craftSucceeded = false;
                        break;
                    }
                }
                if (output.fluid().isPresent()) {
                    FluidStack leftover = acceptFluid(output.fluid().get(), true);
                    if (!leftover.isEmpty()) {
                        craftSucceeded = false;
                        break;
                    }
                }
            }
        }

        if (craftSucceeded) {
            setContainer(getContainer().shrunken(recipe.getMainInput().amount()));

            for (TrailRecipe.ConsumptionEntry entry : consumption.get()) {
                if (entry.notConsume()) {
                    catalysts.add(entry);
                }
                else if (entry.damageItem() > 0) {
                    entry.device().damageItem(entry.damageItem());
                }
                else {
                    entry.device().consume(entry.amount());
                }
            }


            for (OutputRoller.RolledOutput output : rolled) {
                output.item().ifPresent(item -> acceptItem(item, false));
                output.fluid().ifPresent(fluid -> acceptFluid(fluid, false));
            }

            if (getContainer().isEmpty()) {
                flushBuffer();
                setIdle();
            } else {
                startProcessing(level, new TrailRecipeInput(getContainer(), input.beams()));
            }

            onCraftCompleted(level, getPos(), input.beams());
        }
        else {
            for (OutputRoller.RolledOutput output : rolled) {
                output.item().ifPresent(item -> pendingOutputs.add(item));
                output.fluid().ifPresent(fluid -> pendingOutputs.add(fluid));
            }
            pendingOutputs.setRecipeId(recipeHolder.id());
        }

        cachedCatalysts = Optional.of(catalysts);
        blockEntity.updated();
        return craftSucceeded;
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

    protected void whilePending(ServerLevel level, BlockPos pos, List<Beam> beams) {
        double Sx = pos.getX() + 0.5;
        double Sy = pos.getY() + 1.0;
        double Sz = pos.getZ() + 0.5;

        double Tx = Sx + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);
        double Ty = pos.getY() + 1.0;
        double Tz = Sz + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);

        double Vx = Tx - Sx;
        double Vy = Ty - Sy;
        double Vz = Tz - Sz;

        level.sendParticles(ParticleTypes.SMOKE, Sx, Sy, Sz, 1, Vx, Vy, Vz, 0.02);
    }

    protected abstract void onCraftCompleted(ServerLevel level, BlockPos pos, List<Beam> beams);

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putInt("processing_time", processingTime);
        nbt.putBoolean("met_daylight_condition", metDayLightCondition);

        CompoundTag pendingOutputsTag = new CompoundTag();
        pendingOutputs.write(pendingOutputsTag, registries, clientPacket);
        if (!pendingOutputsTag.isEmpty()) {
            nbt.put("pending_outputs", pendingOutputsTag);
        }
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        processingTime = nbt.getInt("processing_time");
        metDayLightCondition = nbt.getBoolean("met_daylight_condition");

        if (nbt.contains("pending_outputs")) {
            CompoundTag pendingOutputsTag = nbt.getCompound("pending_outputs");
            pendingOutputs.read(pendingOutputsTag, registries, clientPacket);
        }
        else pendingOutputs.clear();
    }

    protected abstract void syncProgressToClient(ServerLevel level);
}
