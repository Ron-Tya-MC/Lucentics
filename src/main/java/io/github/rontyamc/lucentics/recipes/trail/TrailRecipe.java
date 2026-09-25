package io.github.rontyamc.lucentics.recipes.trail;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.beam.Beam;
import io.github.rontyamc.lucentics.common.beam.node.BeamNode;
import io.github.rontyamc.lucentics.common.beam.DeviceSlot;
import io.github.rontyamc.lucentics.common.beam.INodeDevice;
import io.github.rontyamc.lucentics.common.recipe.BaseRecipe;
import io.github.rontyamc.lucentics.common.recipe.IRecipeInfo;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.common.util.BlockUtil;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrailRecipe extends BaseRecipe<TrailRecipeInput, RecipeArguments> {
    public TrailRecipe(IRecipeInfo recipeInfo, RecipeArguments args) {
        super(recipeInfo, args);
    }

    @Override
    public boolean matches(TrailRecipeInput input, Level level) {
        return resolveConsumption(input, level).isPresent();
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
        if (ordering.requiredType().isPresent() && !device.type().isKindOf(ordering.requiredType().get())) {
            return false;
        }

        return device.stack().content().map(ordering.ingredient()::test, ordering.ingredient()::test);
    }

    private static List<DeviceSlot> collectDevices(Beam beam, Level level) {
        List<DeviceSlot> devices = new ArrayList<>();
        for (BeamNode node : beam.nodes()) {
            if (!BlockUtil.isIdInTag(node.blockId(), LucenticsTagRegister.LucenticsBTags.PRISM_RITUAL.tag, level)) continue;

            BlockPos devicePos = node.pos().above();
            BlockEntity be = level.getBlockEntity(devicePos);
            if (!(be instanceof BaseBlockEntity base)) continue;

            base.findBehavior(b -> b instanceof INodeDevice).ifPresent(behavior -> {
                INodeDevice device = (INodeDevice) behavior;
                devices.add(new DeviceSlot(behavior.getType(), device.getStack(), device));
            });
        }
        return devices;
    }

    public Optional<List<ConsumptionEntry>> resolveConsumption(TrailRecipeInput input, Level level) {
        if (!mainInput.test(input.mainInput())) return Optional.empty();
        if (input.beams().size() != trailInputs.size()) return Optional.empty();

        int size = trailInputs.size();

        List<List<DeviceSlot>> perBeamDevices = new ArrayList<>();
        for (Beam beam : input.beams()) perBeamDevices.add(collectDevices(beam, level));

        int[] assignment = new int[size];
        boolean[] used = new boolean[size];

        if (!backtrackWithAssignment(input.beams(), perBeamDevices, used, assignment)) {
            return Optional.empty();
        }

        List<ConsumptionEntry> entries = new ArrayList<>();
        for (int beamIndex = 0; beamIndex < assignment.length; beamIndex++) {
            int trailIndex = assignment[beamIndex];
            List<DeviceSlot> devices = perBeamDevices.get(beamIndex);
            List<RecipeArguments.OrderingInput> orderings = trailInputs.get(trailIndex).inputs();

            for (int i = 0; i < devices.size(); i++) {
                RecipeArguments.OrderingInput ordering = orderings.get(i);
                int amount = ordering.ingredient().amount();
                entries.add(new ConsumptionEntry(devices.get(i).device(), amount, ordering.damageItem(), ordering.notConsume()));
            }
        }
        return Optional.of(entries);
    }

    public record ConsumptionEntry(INodeDevice device, int amount, int damageItem, boolean notConsume) {}

    private boolean backtrackWithAssignment(List<Beam> beams, List<List<DeviceSlot>> perBeamDevices, boolean[] used, int[] assignment) {
        for (int i = 0; i < beams.size(); i++) {
            Beam beam = beams.get(i);
            List<DeviceSlot> devices = perBeamDevices.get(i);

            boolean matched = false;
            for (int j = 0; j < trailInputs.size(); j++) {
                if (used[j]) continue;
                if (trailMatches(beam, devices, trailInputs.get(j))) {
                    used[j] = true;
                    assignment[i] = j;
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }
        return true;
    }
}
