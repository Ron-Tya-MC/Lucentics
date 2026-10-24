package io.github.rontyamc.lucentics.datagen.builders.advancement;

import com.google.common.collect.Maps;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.advancement.LucenticsEventTrigger;
import io.github.rontyamc.lucentics.common.advancement.AdvancementEventType;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.extensions.IAdvancementBuilderExtension;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AdvancementBuilder implements IAdvancementBuilderExtension {
    private ResourceLocation id;
    @Nullable
    private ResourceLocation parentLocation;
    @Nullable
    private AdvancementHolder parent;
    @Nullable
    private DisplayInfo displayInfo;
    private AdvancementRewards rewards = AdvancementRewards.EMPTY;
    private Map<String, Criterion<?>> criteria = Maps.newLinkedHashMap();
    @Nullable
    private AdvancementRequirements requirements;
    private AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;

    private AdvancementBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static AdvancementBuilder create(ResourceLocation id) {
        return new AdvancementBuilder(id);
    }

    public static AdvancementBuilder create(String id) {
        return new AdvancementBuilder(Lucentics.path(id));
    }

    public AdvancementBuilder parent(AdvancementHolder parent) {
        this.parent = parent;
        return parentLocation(parent.id());
    }

    public AdvancementBuilder parentLocation(ResourceLocation parentLocation) {
        this.parentLocation = parentLocation;
        return this;
    }

    public AdvancementBuilder displayInfo(DisplayInfo displayInfo) {
        this.displayInfo = displayInfo;
        return this;
    }

    public AdvancementBuilder displayInfo(ItemStack displayStack, Component title, Component description) {
        return displayInfo(DisplayBuilder.create(displayStack).title(title).description(description).build());
    }

    public AdvancementBuilder displayInfo(ItemStack displayStack) {
        return displayInfo(displayStack, this.translatable(), this.translatable("desc"));
    }

    public AdvancementBuilder displayInfo(Supplier<? extends ItemLike> item) {
        return displayInfo(new ItemStack(item.get()));
    }

    public AdvancementBuilder rewards(AdvancementRewards.Builder pRewardsBuilder) {
        return this.rewards(pRewardsBuilder.build());
    }

    public AdvancementBuilder rewards(AdvancementRewards rewards) {
        this.rewards = rewards;
        return this;
    }

    public AdvancementBuilder criterion(Criterion<?> criterion) {
        return this.addCriterion(id.getPath(), criterion);
    }

    public AdvancementBuilder addCriterion(String critKey, Criterion<?> criterion) {
        if (this.criteria.containsKey(critKey)) {
            throw new IllegalArgumentException("Duplicate criterion " + critKey);
        }
        else {
            this.criteria.put(critKey, criterion);
            return this;
        }
    }

    public AdvancementBuilder requirements(AdvancementRequirements requirements) {
        this.requirements = requirements;
        return this;
    }

    public AdvancementBuilder requirements(AdvancementRequirements.Strategy requirementsStrategy) {
        this.requirementsStrategy = requirementsStrategy;
        return this;
    }

    public AdvancementBuilder obtain(ItemLike item) {
        return this.displayInfo(new ItemStack(item)).hasItemCriterion(item);
    }

    public AdvancementBuilder obtain(ItemStack item) {
        return obtain(item.getItem());
    }

    public AdvancementBuilder obtain(Supplier<? extends ItemLike> item) {
        return obtain(new ItemStack(item.get()));
    }

    public AdvancementBuilder obtain(Supplier<? extends ItemLike> item, TagKey<Item> tagKey) {
        return this.displayInfo(item).hasItemCriterion(tagKey);
    }

    public AdvancementBuilder obtain(ItemStack displayStack, TagKey<Item> tagKey) {
        return this.displayInfo(displayStack).hasItemCriterion(tagKey);
    }

    public AdvancementBuilder hasItemCriterion(ItemLike item) {
        return this.addCriterion(ItemUtil.getId(item).getPath(),
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item)));
    }

    public AdvancementBuilder hasItemCriterion(TagKey<Item> tagKey) {
        return this.addCriterion(tagKey.location().getPath(),
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tagKey)));
    }

    // レシピ以外の引数が欲しくなる時はいずれ来るだろうが、それまではこれで対処
    public AdvancementBuilder on(AdvancementEventType event, ResourceLocation... recipes) {
        return this.addCriterion(event.getId().toString(),
                LucenticsEventTrigger.TriggerInstance.criterion(event, List.of(recipes)));
    }

    public AdvancementBuilder on(Supplier<? extends AdvancementEventType> event, ResourceLocation... recipes) {
        return on(event.get(), recipes);
    }

    public Component translatable() {
        return Component.translatable("advancement." + id.getNamespace() + "." + id.getPath().replace('/', '.'));
    }

    public Component translatable(String type) {
        return Component.translatable("advancement." + id.getNamespace() + "." + id.getPath().replace('/', '.') + "." + type);
    }

    public Advancement build() {
        if (this.requirements == null) {
            this.requirements = this.requirementsStrategy.create(this.criteria.keySet());
        }
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("Advancement \"" + id + "\" has no criteria " + this);
        }
        return new Advancement(Optional.ofNullable(this.parentLocation), Optional.ofNullable(this.displayInfo), this.rewards, this.criteria, this.requirements, false);
    }

    public AdvancementHolder save(Consumer<AdvancementHolder> consumer, ResourceLocation id) {
        Advancement advancement = this.build();
        AdvancementHolder holder = new AdvancementHolder(id, advancement);
        consumer.accept(holder);
        return holder;
    }

    public AdvancementHolder save(Consumer<AdvancementHolder> consumer) {
        return save(consumer, id);
    }
}
