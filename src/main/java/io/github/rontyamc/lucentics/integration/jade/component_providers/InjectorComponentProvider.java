package io.github.rontyamc.lucentics.integration.jade.component_providers;

import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBehavior;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBlockEntity;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRecipeInput;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public enum InjectorComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data.isEmpty()) return;

        IElementHelper helper = IElementHelper.get();
        Level level = accessor.getLevel();

        ItemStack container = data.contains("container")
                ? ItemStack.parse(level.registryAccess(), data.getCompound("container")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;

        if (!container.isEmpty()) {
            tooltip.add(helper.smallItem(container.copyWithCount(1)));
            tooltip.append(helper.text(Component.literal(container.getCount() + "x ").append(container.getHoverName())));
        }

        if (data.contains("buffer")) {
            ItemStack buffer = ItemStack.parse(accessor.getLevel().registryAccess(), data.getCompound("buffer"))
                    .orElse(ItemStack.EMPTY);
            if (!buffer.isEmpty()) {
                tooltip.add(helper.smallItem(buffer.copyWithCount(1)));
                tooltip.append(helper.text(Component.literal(buffer.getCount() + "x ").append(buffer.getHoverName())));
            }
        }

        if (!container.isEmpty()) {
            level.getRecipeManager()
                    .getRecipeFor(LucenticsRecipeTypesRegister.INJECTION_TYPE.get(),
                            new InjectorRecipeInput(container), level)
                    .ifPresent(recipeHolder -> {
                        int required = recipeHolder.value().getDayLightCondition();
                        int current = InjectorBehavior.getDaylight(level, accessor.getPosition());
                        boolean met = current >= required;
                        if (!met) tooltip.add(Component.translatable("jade.lucentics.injector.insufficient_daylight")
                                .withStyle(ChatFormatting.RED));
                    });
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof InjectorBlockEntity be)) return;
        var behavior = be.getInjectorBehavior();

        if (!behavior.getContainer().isEmpty()) {
            data.put("container", behavior.getContainer().save(accessor.getLevel().registryAccess(), new CompoundTag()));
        }
        if (!behavior.getBuffer().isEmpty()) {
            data.put("buffer", behavior.getBuffer().save(accessor.getLevel().registryAccess(), new CompoundTag()));
        }
        data.putInt("processing_time", Math.max(behavior.getProcessingTime(), 0));
    }

    @Override
    public net.minecraft.resources.ResourceLocation getUid() {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("lucentics", "injector");
    }
}
