package io.github.rontyamc.lucentics.common.recipe;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LucenticsRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Lucentics.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, Lucentics.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InjectorRecipe>> INJECTOR_SERIALIZER = SERIALIZER.register("injector", InjectorRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<InjectorRecipe>> INJECTOR_TYPE = TYPE.register("injector", () -> new RecipeType<InjectorRecipe>() {
        @Override
        public String toString() {
            return "injector";
        }
    });

    public static void register(IEventBus bus) {
        SERIALIZER.register(bus);
        TYPE.register(bus);
    }
}
