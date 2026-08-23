package io.github.rontyamc.lucentics.integration.jade.component_providers;

import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public enum EngravingTableComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

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
        if (!(accessor.getBlockEntity() instanceof EngravingTableBlockEntity be)) return;
        var behavior = be.getEngravingTableBehavior();

        data.putBoolean("met_daylight_condition", behavior.metDayLightCondition());
    }

    @Override
    public net.minecraft.resources.ResourceLocation getUid() {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("lucentics", "engraving_table");
    }
}
