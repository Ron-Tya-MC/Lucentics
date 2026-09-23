package io.github.rontyamc.lucentics.blocks.prism.prism_dyeing;

import io.github.rontyamc.lucentics.blocks.mixing_table.MixingTableBlockEntity;
import io.github.rontyamc.lucentics.blocks.prism.PrismBehavior;
import io.github.rontyamc.lucentics.client.particle.FlowingGlowParticleOptions;
import io.github.rontyamc.lucentics.client.particle.GlowParticleOptions;
import io.github.rontyamc.lucentics.common.beam.node.BeamNode;
import io.github.rontyamc.lucentics.common.beam.node.NodeActivationContext;
import io.github.rontyamc.lucentics.common.beam.node.NodeScheduleHelper;
import io.github.rontyamc.lucentics.common.beam.node.PrismQualifier;
import io.github.rontyamc.lucentics.common.beam.particle.BeamParticles;
import io.github.rontyamc.lucentics.common.beam.particle.IFlowingParticleSchedulable;
import io.github.rontyamc.lucentics.common.beam.particle.IFlowingParticleScheduleTicker;
import io.github.rontyamc.lucentics.common.beam.particle.ScheduledFlowingParticleHelper;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts.ItemExportingContext;
import io.github.rontyamc.lucentics.common.beam.transfer.ExportingContexts.ItemExportingContext.ItemExportingInfo;
import io.github.rontyamc.lucentics.common.beam.transfer.ImportingContexts.ItemImportingContext;
import io.github.rontyamc.lucentics.common.beam.transfer.TransferManager;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import io.github.rontyamc.lucentics.common.util.ParticleUtil;
import io.github.rontyamc.lucentics.common.util.TransferUtil;
import io.github.rontyamc.lucentics.recipes.dyeing.process.DyeingProgressEntry;
import io.github.rontyamc.lucentics.recipes.dyeing.process.DyeingProgressHelper;
import io.github.rontyamc.lucentics.recipes.dyeing.process.DyeingRecipeResolver;
import io.github.rontyamc.lucentics.registers.LucenticsFluidRegister;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;
import java.util.Optional;

public class PrismDyeingBehavior extends PrismBehavior implements IFlowingParticleScheduleTicker {
    public static final PrismDyeingBehavior INSTANCE = new PrismDyeingBehavior();
    public static final int PARTICLE_INTERVAL = 8;
    public static final int PARTICLE_LINGER = 8;

    public static final RandomSource RANDOM_SOURCE = RandomSource.create();

    protected PrismDyeingBehavior() {}

    @Override
    public int getParticleLinger() {
        return PARTICLE_LINGER;
    }

    @Override
    public void activate(ServerLevel level, BlockPos prismPos, BlockPos interactPos, NodeActivationContext context) {
        LevelChunk chunk = level.getChunkAt(prismPos);

        if (!level.getBlockState(interactPos).is(LucenticsTagRegister.LucenticsBTags.PEDESTAL_RITUAL.tag)) {
            DyeingProgressHelper.clear(chunk, prismPos);
            return;
        }
        Optional<IItemHandler> inputHandler = TransferUtil.getItemHandler(level, interactPos);
        if (inputHandler.isEmpty()) {
            DyeingProgressHelper.clear(chunk, prismPos);
            return;
        }
        ItemImportingContext importingContext = TransferUtil.firstImportable(inputHandler.get());
        ItemStack dyeable = importingContext.itemStack();
        if (dyeable.isEmpty()) {
            DyeingProgressHelper.clear(chunk, prismPos);
            return;
        }

        Optional<ColorSupply> supply = findDyeSupply(level, context.nodesAhead());
        if (supply.isEmpty()) {
            DyeingProgressHelper.clear(chunk, prismPos);
            return;
        }

        Optional<DyeingRecipeResolver.Result> recipe =
                DyeingRecipeResolver.resolve(level, dyeable, supply.get().color());
        if (recipe.isEmpty()) {
            DyeingProgressHelper.clear(chunk, prismPos);
            return;
        }
        if (supply.get().containedAmount() < recipe.get().liquidAmount()) {
            DyeingProgressHelper.clear(chunk, prismPos);
            return;
        }

        ItemExportingContext exportingContext =
                TransferManager.planItemExport(level, recipe.get().output(),
                        context.nodesAhead().subList(supply.get().supplyIndex(), context.nodesAhead().size()));

        if (!exportingContext.hasAnyExport()) {
            DyeingProgressHelper.clear(chunk, prismPos);
            return;
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(dyeable.getItem());
        String colorName = supply.get().color().getSerializedName();

        DyeingProgressEntry existing = DyeingProgressHelper.get(chunk, prismPos);
        boolean sameContext = existing != null
                && existing.itemId().equals(itemId)
                && existing.color().equals(colorName);

        int remaining = sameContext ? Math.max(existing.processingTime() - 1, 0) : recipe.get().processingDuration();
        whileCrafting(level, interactPos);

        if (remaining == 0) {
            craft(level, prismPos, importingContext, supply.get(), exportingContext, recipe.get(), interactPos, context.color());
            DyeingProgressHelper.clear(chunk, prismPos);
            return;
        }

        DyeingProgressHelper.set(chunk, prismPos, itemId, colorName, remaining);
    }

    private void craft(ServerLevel level, BlockPos prismPos, ItemImportingContext importingContext,
                       ColorSupply supply, ItemExportingContext exportingContext,
                       DyeingRecipeResolver.Result recipe, BlockPos interactPos, Colors color) {
        importingContext.handler().extractItem(importingContext.slot(), 1, false);

        FluidStack currentDye = supply.handler().getFluidInTank(supply.tank());
        supply.handler().drain(currentDye.copyWithAmount(recipe.liquidAmount()), IFluidHandler.FluidAction.EXECUTE);

        exportingContext.commit();

        if (!exportingContext.leftover().isEmpty()) {
            // 無いとは思うが
            ItemUtil.dropItem(level, prismPos, exportingContext.leftover());
        }

        onCrafted(level, interactPos, prismPos, exportingContext, color);
    }

    private void whileCrafting(ServerLevel level, BlockPos pos) {
        float p = Mth.lerp(RANDOM_SOURCE.nextFloat(), 1.2f, 1.5f);
        level.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.1f, p);
    }

    private void onCrafted(ServerLevel level, BlockPos interactPos, BlockPos prismPos, ItemExportingContext exportingContext, Colors beamColor) {
        float p = Mth.lerp(RANDOM_SOURCE.nextFloat(), 1.7f, 2.0f);
        level.playSound(null, interactPos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.1f, p);

        int rgb = Colors.BLUE.getColorCode();
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;

        double Sx = interactPos.getX() + 0.5;
        double Sy = interactPos.getY() + 0.8;
        double Sz = interactPos.getZ() + 0.5;

        double Tx = Sx + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);
        double Ty = interactPos.getY() + 1.0;
        double Tz = Sz + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);

        double Vx = Tx - Sx;
        double Vy = Ty - Sy;
        double Vz = Tz - Sz;

        level.sendParticles(new GlowParticleOptions(r,g,b), Sx, Sy, Sz, 5, Vx, Vy, Vz, 0.01);

        if (BeamParticles.canSpawnOnThisTick(level, prismPos)) {
            for (ItemExportingInfo info : exportingContext.infos()) {
                ParticleUtil.spawnAndScheduleFlowing(level, interactPos, prismPos, info.targetNode(), beamColor, PARTICLE_INTERVAL);
            }
        }
    }

    private record ColorSupply(Colors color, IFluidHandler handler, int tank, int containedAmount, int supplyIndex) {}

    private Optional<ColorSupply> findDyeSupply(ServerLevel level, List<BeamNode> nodesAhead) {
        for (int i = 0; i < nodesAhead.size(); i++) {
            BeamNode node = nodesAhead.get(i);
            Optional<BlockPos> devicePos = TransferManager.resolveDevicePos(node, level, PrismQualifier.PRISM_RITUAL);
            if (devicePos.isEmpty()) continue;

            BlockEntity blockEntity = level.getBlockEntity(devicePos.get());
            if (!(blockEntity instanceof MixingTableBlockEntity mixing)) continue;

            IFluidHandler handler = mixing.getMixingTableBehavior().getAdminFHandler();

            for (int tank = 0; tank < handler.getTanks(); tank++) {
                FluidStack content = handler.getFluidInTank(tank);
                if (content.isEmpty()) continue;

                Optional<Colors> color = LucenticsFluidRegister.getColor(content.getFluid());
                if (color.isPresent()) {
                    return Optional.of(new ColorSupply(color.get(), handler, tank, content.getAmount(), i));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void tickSchedule(ServerLevel level, BlockPos prismPos) {
        LevelChunk chunk = level.getChunkAt(prismPos);

        if (NodeScheduleHelper.tick(chunk, prismPos, IFlowingParticleSchedulable.SCHEDULE_ID) >= 0) {
            FlowingGlowParticleOptions options = ScheduledFlowingParticleHelper.get(chunk, prismPos).options();

            float d = RANDOM_SOURCE.nextFloat() * 0.3f - 0.15f;
            FlowingGlowParticleOptions newOptions =
                    new FlowingGlowParticleOptions(options.waypoints(), options.red(), options.blue(), options.green(), options.duration(),
                            RANDOM_SOURCE.nextFloat() * 0.3f + 0.15f, new Vec3(d, d, d));

            BeamParticles.spawnFlowing(level, newOptions, prismPos);
        }
    }
}
