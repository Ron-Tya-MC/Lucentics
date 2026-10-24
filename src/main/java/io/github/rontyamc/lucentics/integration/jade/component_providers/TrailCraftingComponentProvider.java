package io.github.rontyamc.lucentics.integration.jade.component_providers;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import io.github.rontyamc.lucentics.common.behavior.TrailCraftingBehavior;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.Optional;

public class TrailCraftingComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    public static final TrailCraftingComponentProvider INSTANCE = new TrailCraftingComponentProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data.isEmpty()) return;

        boolean met = data.contains("met_daylight_condition") && data.getBoolean("met_daylight_condition");

        if (!met) {
            tooltip.add(Component.translatable("jade.lucentics.injector.insufficient_daylight").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof BaseBlockEntity be)) return;
        Optional<BlockEntityBehavior> foundBehavior = be.findBehavior(behavior -> behavior instanceof TrailCraftingBehavior);
        if (foundBehavior.isEmpty()) return;
        TrailCraftingBehavior behavior = (TrailCraftingBehavior) foundBehavior.get();

        data.putBoolean("met_daylight_condition", behavior.metDayLightCondition());
    }

    @Override
    public ResourceLocation getUid() {
        return Lucentics.path("trail_crafting");
    }
}
