package io.github.rontyamc.lucentics.integration.jei;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.integration.jei.recipe_categories.*;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingRecipe;
import io.github.rontyamc.lucentics.recipes.dyeing.DyeingRecipeCollector;
import io.github.rontyamc.lucentics.recipes.dyeing.DyeingRecipeEntry;
import io.github.rontyamc.lucentics.recipes.injecting.InjectingRecipe;
import io.github.rontyamc.lucentics.recipes.trail.TrailRecipe;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

@JeiPlugin
public class LucenticsJEIIntegration implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = Lucentics.defaultLocation("jei_plugin");

    public static final RecipeType<InjectingRecipe> INJECTING =
            RecipeType.create(Lucentics.MOD_ID, "injecting", InjectingRecipe.class);
    public static final RecipeType<TrailRecipe> ENGRAVING =
            RecipeType.create(Lucentics.MOD_ID, "engraving", TrailRecipe.class);
    public static final RecipeType<CrushingRecipe> CRUSHING =
            RecipeType.create(Lucentics.MOD_ID, "crushing", CrushingRecipe.class);
    public static final RecipeType<TrailRecipe> MILLING =
            RecipeType.create(Lucentics.MOD_ID, "milling", TrailRecipe.class);
    public static final RecipeType<TrailRecipe> MIXING =
            RecipeType.create(Lucentics.MOD_ID, "mixing", TrailRecipe.class);
    public static final RecipeType<TrailRecipe> ASSEMBLING =
            RecipeType.create(Lucentics.MOD_ID, "assembling", TrailRecipe.class);
    public static final RecipeType<DyeingRecipeEntry> DYEING =
            RecipeType.create(Lucentics.MOD_ID, "dyeing", DyeingRecipeEntry.class);

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        CommonParts.buildCommonParts(guiHelper);

        registration.addRecipeCategories(new InjectingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new EngravingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new CrushingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new MillingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new MixingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new AssemblingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new DyeingRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        registerStream(registration, level, LucenticsRecipeTypesRegister.INJECTING_TYPE.get(), INJECTING);
        registerStream(registration, level, LucenticsRecipeTypesRegister.ENGRAVING_TYPE.get(), ENGRAVING);
        registerStream(registration, level, LucenticsRecipeTypesRegister.CRUSHING_TYPE.get(), CRUSHING);
        registerStream(registration, level, LucenticsRecipeTypesRegister.MILLING_TYPE.get(), MILLING);
        registerStream(registration, level, LucenticsRecipeTypesRegister.MIXING_TYPE.get(), MIXING);
        registerStream(registration, level, LucenticsRecipeTypesRegister.ASSEMBLING_TYPE.get(), ASSEMBLING);

        registration.addRecipes(DYEING, DyeingRecipeCollector.collect(level.getRecipeManager(), level.registryAccess()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(INJECTING, LucenticsBlockRegister.INJECTOR);

        registration.addRecipeCatalysts(ENGRAVING, LucenticsBlockRegister.ENGRAVING_TABLE);
        registration.addRecipeCatalysts(ENGRAVING, LucenticsBlockRegister.EMITTER);
        registration.addRecipeCatalysts(ENGRAVING, LucenticsBlockRegister.PRISM_RITUAL);

        registration.addRecipeCatalysts(MILLING, LucenticsBlockRegister.MILLING_TABLE);
        registration.addRecipeCatalysts(MILLING, LucenticsBlockRegister.EMITTER);
        registration.addRecipeCatalysts(MILLING, LucenticsBlockRegister.PRISM_RITUAL);

        registration.addRecipeCatalysts(MIXING, LucenticsBlockRegister.MIXING_TABLE);
        registration.addRecipeCatalysts(MIXING, LucenticsBlockRegister.EMITTER);
        registration.addRecipeCatalysts(MIXING, LucenticsBlockRegister.PRISM_RITUAL);

        registration.addRecipeCatalysts(ASSEMBLING, LucenticsBlockRegister.ASSEMBLING_TABLE);
        registration.addRecipeCatalysts(ASSEMBLING, LucenticsBlockRegister.EMITTER);
        registration.addRecipeCatalysts(ASSEMBLING, LucenticsBlockRegister.PRISM_RITUAL);

        registration.addRecipeCatalysts(DYEING, LucenticsBlockRegister.PRISM_DYEING);
        registration.addRecipeCatalysts(DYEING, LucenticsBlockRegister.MIXING_TABLE);
    }

    private<I extends RecipeInput, T extends Recipe<I>> void registerStream(IRecipeRegistration registration, ClientLevel level, net.minecraft.world.item.crafting.RecipeType<T> typeMC, RecipeType<T> typeJEI) {
        List<RecipeHolder<T>> recipes =
                level.getRecipeManager().getAllRecipesFor(typeMC);
        registration.addRecipes(
                typeJEI,
                recipes.stream().map(RecipeHolder::value).toList()
        );
    }

    public static String makeSecond(int tick) {
        int dInt;
        float dFloat;
        if (tick % 20 == 0) {
            dInt = tick / 20;
            return String.valueOf(dInt);
        }
        else {
            dFloat = tick / 20.0F;
            return String.valueOf(dFloat);
        }
    }
}
