package io.github.rontyamc.lucentics.common.recipe;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRecipe;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRecipeInput;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LucenticsRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Lucentics.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, Lucentics.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BaseRecipe<InjectorRecipeInput, RecipeArguments>>> INJECTOR_SERIALIZER =
            SERIALIZER.register("injector", () -> BaseRecipe.createSerializer(INJECTOR_INFO));
    public static final DeferredHolder<RecipeType<?>, RecipeType<BaseRecipe<InjectorRecipeInput, RecipeArguments>>> INJECTOR_TYPE =
            TYPE.register("injector", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Lucentics.MOD_ID, "injector")));
    public static final RecipeInfo<InjectorRecipeInput, BaseRecipe<InjectorRecipeInput, RecipeArguments>> INJECTOR_INFO =
            new RecipeInfo<>(
                    ResourceLocation.fromNamespaceAndPath(Lucentics.MOD_ID, "injector"),
                    INJECTOR_TYPE.get(),
                    INJECTOR_SERIALIZER.get()
            );

    public static void register(IEventBus bus) {
        SERIALIZER.register(bus);
        TYPE.register(bus);
    }
}
