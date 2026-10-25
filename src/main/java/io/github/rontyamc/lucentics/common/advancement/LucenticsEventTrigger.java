package io.github.rontyamc.lucentics.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rontyamc.lucentics.common.util.MiscUtil;
import io.github.rontyamc.lucentics.registers.LucenticsTriggerRegister;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class LucenticsEventTrigger extends SimpleCriterionTrigger<LucenticsEventTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, AdvancementEventType event) {
        this.trigger(player, event, null);
    }

    public void trigger(ServerPlayer player, AdvancementEventType event, @Nullable ResourceLocation recipeId) {
        this.trigger(player, instance -> instance.matches(event, recipeId));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, AdvancementEventType event,
                                  Optional<List<ResourceLocation>> recipes) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                AdvancementEventType.CODEC.fieldOf("event").forGetter(TriggerInstance::event),
                MiscUtil.singleOrList(ResourceLocation.CODEC).optionalFieldOf("recipes").forGetter(TriggerInstance::recipes)
        ).apply(ins, TriggerInstance::new));

        public static Criterion<TriggerInstance> criterion(AdvancementEventType event) {
            return criterion(event, List.of());
        }

        public static Criterion<TriggerInstance> criterion(AdvancementEventType event, List<ResourceLocation> recipes) {
            Optional<List<ResourceLocation>> recipeCondition = recipes.isEmpty() ? Optional.empty() : Optional.of(List.copyOf(recipes));
            return LucenticsTriggerRegister.EVENT.get().createCriterion(new TriggerInstance(Optional.empty(), event, recipeCondition));
        }

        public static Criterion<TriggerInstance> criterion(Supplier<? extends AdvancementEventType> event) {
            return criterion(event.get());
        }

        public static Criterion<TriggerInstance> criterion(Supplier<? extends AdvancementEventType> event, List<ResourceLocation> recipes) {
            return criterion(event.get(), recipes);
        }

        public boolean matches(AdvancementEventType event, @Nullable ResourceLocation recipeId) {
            if (this.event != event) return false;
            return this.recipes.map(list -> recipeId != null && list.contains(recipeId)).orElse(true);
        }
    }
}
