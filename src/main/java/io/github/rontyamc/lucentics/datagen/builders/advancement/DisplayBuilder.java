package io.github.rontyamc.lucentics.datagen.builders.advancement;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class DisplayBuilder {
    private ItemStack icon;
    private Component title;
    private Component description;
    @Nullable
    private ResourceLocation background;
    private AdvancementType type = AdvancementType.TASK;
    private boolean showToast = true;
    private boolean announceToChat = true;
    private boolean hidden = false;

    public DisplayBuilder(ItemStack icon) {
        this.icon = icon;
    }

    public static DisplayBuilder create(ItemStack icon) {
        return new DisplayBuilder(icon);
    }

    public static DisplayBuilder create(Supplier<? extends ItemLike> item) {
        return new DisplayBuilder(new ItemStack(item.get()));
    }

    public DisplayBuilder title(Component title) {
        this.title = title;
        return this;
    }

    public DisplayBuilder title(String title) {
        this.title = Component.translatable(title);
        return this;
    }

    public DisplayBuilder description(Component description) {
        this.description = description;
        return this;
    }

    public DisplayBuilder description(String description) {
        this.description = Component.translatable(description);
        return this;
    }

    public DisplayBuilder background(ResourceLocation background) {
        this.background = background;
        return this;
    }

    public DisplayBuilder type(AdvancementType type) {
        this.type = type;
        return this;
    }

    public DisplayBuilder showToast(boolean showToast) {
        this.showToast = showToast;
        return this;
    }

    public DisplayBuilder announceToChat(boolean announceToChat) {
        this.announceToChat = announceToChat;
        return this;
    }

    public DisplayBuilder hidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public DisplayInfo build() {
        return new DisplayInfo(icon, title, description, Optional.ofNullable(background), type, showToast, announceToChat, hidden);
    }
}
