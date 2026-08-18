package io.github.rontyamc.lucentics.integration.jade.component_providers;

import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlockEntity;
import io.github.rontyamc.lucentics.blocks.pedestals.PedestalBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public enum EmitterComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data.isEmpty() || !data.contains("lens")) return;

        IElementHelper helper = IElementHelper.get();
        Level level = accessor.getLevel();

        ItemStack content = ItemStack.parse(level.registryAccess(), data.getCompound("lens")).orElse(ItemStack.EMPTY);

        tooltip.add(helper.item(content.copyWithCount(1)));
        tooltip.append(helper.text(Component.literal(content.getCount() + "x ").append(content.getHoverName())));
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof EmitterBlockEntity be)) return;
        ItemStack content = be.getContent();
        if (!content.isEmpty()) {
            data.put("lens", content.save(accessor.getLevel().registryAccess(), new CompoundTag()));
        }
    }

    @Override
    public net.minecraft.resources.ResourceLocation getUid() {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("lucentics", "emitter");
    }
}
