package io.github.rontyamc.lucentics.integration.jade.component_providers;

import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableBlockEntity;
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

        IElementHelper helper = IElementHelper.get();
        Level level = accessor.getLevel();

        if (data.contains("container")) {
            ItemStack container = ItemStack.parse(level.registryAccess(), data.getCompound("container")).orElse(ItemStack.EMPTY);
            if (!container.isEmpty()) {
                tooltip.add(helper.smallItem(container.copyWithCount(1)));
                tooltip.append(helper.text(Component.literal(container.getCount() + "x ").append(container.getHoverName())));
            }
        }

        if (data.contains("buffer")) {
            ListTag bufferList = data.getList("buffer", Tag.TAG_COMPOUND);
            for (int i = 0; i < bufferList.size(); i++) {
                ItemStack.parse(level.registryAccess(), bufferList.getCompound(i)).ifPresent(stack -> {
                    tooltip.add(helper.smallItem(stack.copyWithCount(1)));
                    tooltip.append(helper.text(Component.literal(stack.getCount() + "x ").append(stack.getHoverName())));
                });
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof EngravingTableBlockEntity be)) return;
        var behavior = be.getEngravingTableBehavior();
        Level level = accessor.getLevel();

        if (!behavior.getContainer().isEmpty()) {
            data.put("container", behavior.getContainer().save(level.registryAccess(), new CompoundTag()));
        }

        ListTag bufferList = new ListTag();
        for (ItemStack stack : behavior.getBuffer()) {
            if (!stack.isEmpty()) bufferList.add(stack.save(level.registryAccess(), new CompoundTag()));
        }
        data.put("buffer", bufferList);
    }

    @Override
    public net.minecraft.resources.ResourceLocation getUid() {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("lucentics", "engraving_table");
    }
}
