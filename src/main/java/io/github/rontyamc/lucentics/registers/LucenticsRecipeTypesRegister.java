package io.github.rontyamc.lucentics.registers;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableRecipe;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableRecipeInput;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRecipe;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRecipeInput;
import io.github.rontyamc.lucentics.common.recipe.BaseRecipeSerializer;
import io.github.rontyamc.lucentics.common.recipe.RecipeInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LucenticsRecipeTypesRegister {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Lucentics.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, Lucentics.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<InjectorRecipe>> INJECTION_TYPE =
            TYPE.register("injection", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Lucentics.MOD_ID, "injection")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InjectorRecipe>> INJECTION_SERIALIZER =
            SERIALIZER.register("injection", () -> new BaseRecipeSerializer<>(InjectorRecipe::new, LucenticsRecipeTypesRegister.INJECTION_INFO));
    public static final RecipeInfo<InjectorRecipeInput, InjectorRecipe> INJECTION_INFO =
            new RecipeInfo<>(ResourceLocation.fromNamespaceAndPath(Lucentics.MOD_ID, "injection"), INJECTION_TYPE::get, INJECTION_SERIALIZER::get);

    public static final DeferredHolder<RecipeType<?>, RecipeType<EngravingTableRecipe>> ENGRAVING_TYPE =
            TYPE.register("engraving", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Lucentics.MOD_ID, "engraving")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EngravingTableRecipe>> ENGRAVING_SERIALIZER =
            SERIALIZER.register("engraving", () -> new BaseRecipeSerializer<>(EngravingTableRecipe::new, LucenticsRecipeTypesRegister.ENGRAVING_INFO));
    public static final RecipeInfo<EngravingTableRecipeInput, EngravingTableRecipe> ENGRAVING_INFO =
            new RecipeInfo<>(ResourceLocation.fromNamespaceAndPath(Lucentics.MOD_ID, "engraving"), ENGRAVING_TYPE::get, ENGRAVING_SERIALIZER::get);

    public static void register(IEventBus bus) {
        SERIALIZER.register(bus);
        TYPE.register(bus);
    }
}
