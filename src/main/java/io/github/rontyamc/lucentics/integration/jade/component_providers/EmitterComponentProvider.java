package io.github.rontyamc.lucentics.integration.jade.component_providers;

import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlockEntity;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBlockEntity;
import io.github.rontyamc.lucentics.blocks.pedestals.PedestalBlockEntity;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.items.LensItem;
import net.minecraft.ChatFormatting;
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
        Level level = accessor.getLevel();

        ItemStack content = ItemStack.parse(level.registryAccess(), data.getCompound("lens")).orElse(ItemStack.EMPTY);
        Colors color = (!content.isEmpty() && content.getItem() instanceof LensItem) ? ((LensItem) content.getItem()).getColor() : Colors.SUNLIGHT;

        tooltip.add(Component.translatable("term.lucentics.colors." + color.getName()));
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof EmitterBlockEntity be)) return;
        var behavior = be.getEmitterBehavior();

        if (!behavior.getLensContainer().isEmpty()) {
            data.put("lens", behavior.getLensContainer().save(accessor.getLevel().registryAccess(), new CompoundTag()));
        }
    }

    @Override
    public net.minecraft.resources.ResourceLocation getUid() {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("lucentics", "emitter");
    }
}
