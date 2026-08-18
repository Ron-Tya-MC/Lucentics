package io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.beam.Beam;
import io.github.rontyamc.lucentics.common.beam.BeamNode;
import io.github.rontyamc.lucentics.common.beam.DeviceSlot;
import io.github.rontyamc.lucentics.common.beam.INodeDevice;
import io.github.rontyamc.lucentics.common.recipe.BaseRecipe;
import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EngravingTableRecipe extends BaseRecipe<EngravingTableRecipeInput, RecipeArguments> {
    private EngravingTableRecipeInput cachedInput;
    private Optional<List<ConsumptionEntry>> cachedResult;

    public EngravingTableRecipe(IRecipeInfo recipeInfo, RecipeArguments args) {
        super(recipeInfo, args);
    }

    @Override
    public boolean matches(EngravingTableRecipeInput input, Level level) {
        return resolveConsumptionCached(input, level).isPresent();
    }

    private boolean trailMatches(Beam beam, List<DeviceSlot> devices, RecipeArguments.TrailInput trailInput) {
        if (!beam.color().getSerializedName().equals(trailInput.color())) return false;

        List<RecipeArguments.OrderingInput> orderingInputs = trailInput.inputs();
        if (devices.size() != orderingInputs.size()) return false;

        for (int i = 0; i < devices.size(); i++) {
            if (!orderingMatches(devices.get(i), orderingInputs.get(i))) return false;
        }
        return true;
    }

    private boolean orderingMatches(DeviceSlot device, RecipeArguments.OrderingInput ordering) {
        if (ordering.requiredType().isPresent() && !ordering.requiredType().get().equals(device.type())) {
            return false;
        }

        Optional<SizedIngredient> itemIngredient = ordering.ingredient().left();
        return itemIngredient.map(sized -> sized.test(device.stack())).orElse(false);
    }

    private static List<DeviceSlot> collectDevices(Beam beam, Level level) {
        List<DeviceSlot> devices = new ArrayList<>();
        for (BeamNode node : beam.nodes()) {
            BlockPos devicePos = node.pos().above();
            BlockEntity be = level.getBlockEntity(devicePos);
            if (!(be instanceof BaseBlockEntity base)) continue;

            base.findBehavior(b -> b instanceof INodeDevice).ifPresent(behavior -> {
                INodeDevice device = (INodeDevice) behavior;
                devices.add(new DeviceSlot(behavior.getType(), device.getContent(), device));
            });
        }
        return devices;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return outputs.stream().flatMap(o -> o.item().stream()).findFirst().orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack assemble(EngravingTableRecipeInput input, HolderLookup.Provider registries) {
        return getResultItem(registries).copy();
    }

    public Optional<List<ConsumptionEntry>> resolveConsumption(EngravingTableRecipeInput input, Level level) {
        if (mainInput.isEmpty() || !mainInput.get().test(input.mainInput())) return Optional.empty();
        if (input.beams().size() != trailInputs.size()) return Optional.empty();

        List<List<DeviceSlot>> perBeamDevices = new ArrayList<>();
        for (Beam beam : input.beams()) perBeamDevices.add(collectDevices(beam, level));

        int[] assignment = new int[trailInputs.size()];
        boolean[] used = new boolean[trailInputs.size()];
        if (!backtrackWithAssignment(input.beams(), perBeamDevices, used, assignment, 0)) {
            return Optional.empty();
        }

        List<ConsumptionEntry> entries = new ArrayList<>();
        for (int beamIndex = 0; beamIndex < assignment.length; beamIndex++) {
            int trailIndex = assignment[beamIndex];
            List<DeviceSlot> devices = perBeamDevices.get(beamIndex);
            List<RecipeArguments.OrderingInput> orderings = trailInputs.get(trailIndex).inputs();

            for (int i = 0; i < devices.size(); i++) {
                RecipeArguments.OrderingInput ordering = orderings.get(i);
                if (ordering.notConsume()) continue;
                int amount = ordering.ingredient().left().map(SizedIngredient::count).orElse(0);
                entries.add(new ConsumptionEntry(devices.get(i).device(), amount));
            }
        }
        return Optional.of(entries);
    }

    public Optional<List<ConsumptionEntry>> resolveConsumptionCached(EngravingTableRecipeInput input, Level level) {
        if (input == cachedInput) {
            return cachedResult;
        }
        cachedInput = input;
        cachedResult = resolveConsumption(input, level);
        return cachedResult;
    }

    public record ConsumptionEntry(INodeDevice device, int amount) {}

    private boolean backtrackWithAssignment(List<Beam> beams, List<List<DeviceSlot>> perBeamDevices, boolean[] used, int[] assignment, int beamIndex) {
        if (beamIndex == beams.size()) return true;
        Beam beam = beams.get(beamIndex);
        List<DeviceSlot> devices = perBeamDevices.get(beamIndex);

        for (int i = 0; i < trailInputs.size(); i++) {
            if (used[i]) continue;
            if (trailMatches(beam, devices, trailInputs.get(i))) {
                used[i] = true;
                assignment[beamIndex] = i;
                if (backtrackWithAssignment(beams, perBeamDevices, used, assignment, beamIndex + 1)) return true;
                used[i] = false;
            }
        }
        return false;
    }
}
