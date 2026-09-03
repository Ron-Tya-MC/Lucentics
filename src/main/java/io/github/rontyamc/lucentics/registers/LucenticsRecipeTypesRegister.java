package io.github.rontyamc.lucentics.registers;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.recipe.BaseRecipeSerializer;
import io.github.rontyamc.lucentics.common.recipe.RecipeInfo;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingRecipe;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingRecipeInput;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingRecipeSerializer;
import io.github.rontyamc.lucentics.recipes.injecting.InjectingRecipe;
import io.github.rontyamc.lucentics.recipes.injecting.InjectingRecipeInput;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipeInput;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LucenticsRecipeTypesRegister {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Lucentics.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, Lucentics.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<InjectingRecipe>> INJECTING_TYPE =
            TYPE.register("injecting", () -> RecipeType.simple(Lucentics.defaultLocation("injecting")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InjectingRecipe>> INJECTING_SERIALIZER =
            SERIALIZER.register("injecting", () -> new BaseRecipeSerializer<>(InjectingRecipe::new, LucenticsRecipeTypesRegister.INJECTING_INFO));
    public static final RecipeInfo<InjectingRecipeInput, InjectingRecipe> INJECTING_INFO =
            new RecipeInfo<>(Lucentics.defaultLocation("injecting"), INJECTING_TYPE, INJECTING_SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<TrailRecipe>> ENGRAVING_TYPE =
            TYPE.register("engraving", () -> RecipeType.simple(Lucentics.defaultLocation("engraving")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TrailRecipe>> ENGRAVING_SERIALIZER =
            SERIALIZER.register("engraving", () -> new BaseRecipeSerializer<>(TrailRecipe::new, LucenticsRecipeTypesRegister.ENGRAVING_INFO));
    public static final RecipeInfo<TrailRecipeInput, TrailRecipe> ENGRAVING_INFO =
            new RecipeInfo<>(Lucentics.defaultLocation("engraving"), ENGRAVING_TYPE, ENGRAVING_SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<TrailRecipe>> MILLING_TYPE =
            TYPE.register("milling", () -> RecipeType.simple(Lucentics.defaultLocation("milling")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TrailRecipe>> MILLING_SERIALIZER =
            SERIALIZER.register("milling", () -> new BaseRecipeSerializer<>(TrailRecipe::new, LucenticsRecipeTypesRegister.MILLING_INFO));
    public static final RecipeInfo<TrailRecipeInput, TrailRecipe> MILLING_INFO =
            new RecipeInfo<>(Lucentics.defaultLocation("milling"), MILLING_TYPE, MILLING_SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<TrailRecipe>> MIXING_TYPE =
            TYPE.register("mixing", () -> RecipeType.simple(Lucentics.defaultLocation("mixing")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TrailRecipe>> MIXING_SERIALIZER =
            SERIALIZER.register("mixing", () -> new BaseRecipeSerializer<>(TrailRecipe::new, LucenticsRecipeTypesRegister.MIXING_INFO));
    public static final RecipeInfo<TrailRecipeInput, TrailRecipe> MIXING_INFO =
            new RecipeInfo<>(Lucentics.defaultLocation("mixing"), MIXING_TYPE, MIXING_SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<TrailRecipe>> ASSEMBLING_TYPE =
            TYPE.register("assembling", () -> RecipeType.simple(Lucentics.defaultLocation("assembling")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TrailRecipe>> ASSEMBLING_SERIALIZER =
            SERIALIZER.register("assembling", () -> new BaseRecipeSerializer<>(TrailRecipe::new, LucenticsRecipeTypesRegister.ASSEMBLING_INFO));
    public static final RecipeInfo<TrailRecipeInput, TrailRecipe> ASSEMBLING_INFO =
            new RecipeInfo<>(Lucentics.defaultLocation("assembling"), ASSEMBLING_TYPE, ASSEMBLING_SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CrushingRecipe>> CRUSHING_TYPE =
            TYPE.register("crushing", () -> RecipeType.simple(Lucentics.defaultLocation("crushing")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrushingRecipe>> CRUSHING_SERIALIZER =
            SERIALIZER.register("crushing", () -> new CrushingRecipeSerializer<>(CrushingRecipe::new, LucenticsRecipeTypesRegister.CRUSHING_INFO));
    public static final RecipeInfo<CrushingRecipeInput, CrushingRecipe> CRUSHING_INFO =
            new RecipeInfo<>(Lucentics.defaultLocation("crushing"), CRUSHING_TYPE, CRUSHING_SERIALIZER);

    public static void register(IEventBus bus) {
        SERIALIZER.register(bus);
        TYPE.register(bus);
    }
}
