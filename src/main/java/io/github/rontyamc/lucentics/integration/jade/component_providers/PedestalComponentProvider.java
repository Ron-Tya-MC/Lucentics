package io.github.rontyamc.lucentics.integration.jade.component_providers;

import io.github.rontyamc.lucentics.blocks.pedestals.PedestalBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public abstract class PedestalComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data.isEmpty() || !data.contains("content")) return;

        IElementHelper helper = IElementHelper.get();
        Level level = accessor.getLevel();

        ItemStack content = ItemStack.parse(level.registryAccess(), data.getCompound("content")).orElse(ItemStack.EMPTY);

        tooltip.add(helper.smallItem(content.copyWithCount(1)));
        tooltip.append(helper.text(Component.literal(content.getCount() + "x ").append(content.getHoverName())));
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof PedestalBlockEntity be)) return;
        ItemStack content = be.getBehavior().getContent();
        if (!content.isEmpty()) {
            data.put("content", content.save(accessor.getLevel().registryAccess(), new CompoundTag()));
        }
    }

    public abstract ResourceLocation getUid();
}
