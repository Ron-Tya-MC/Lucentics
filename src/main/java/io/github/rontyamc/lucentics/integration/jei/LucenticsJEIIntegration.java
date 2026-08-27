package io.github.rontyamc.lucentics.integration.jei;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableRecipe;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRecipe;
import io.github.rontyamc.lucentics.integration.jei.recipe_categories.EngravingRecipeCategory;
import io.github.rontyamc.lucentics.integration.jei.recipe_categories.InjectingRecipeCategory;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class LucenticsJEIIntegration implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = Lucentics.defaultLocation("jei_plugin");

    private static IJeiRuntime runtime;
    private static List<InjectorRecipe> lastInjectorRecipes = new ArrayList<>();
    private static List<EngravingTableRecipe> lastEngravingRecipes = new ArrayList<>();

    public static final RecipeType<InjectorRecipe> INJECTION =
            RecipeType.create(Lucentics.MOD_ID, "injector", InjectorRecipe.class);
    public static final RecipeType<EngravingTableRecipe> ENGRAVING =
            RecipeType.create(Lucentics.MOD_ID, "engraving", EngravingTableRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new InjectingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new EngravingRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        registerStream(registration, level, LucenticsRecipeTypesRegister.INJECTION_TYPE.get(), INJECTION);
        registerStream(registration, level, LucenticsRecipeTypesRegister.ENGRAVING_TYPE.get(), ENGRAVING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(INJECTION, LucenticsBlockRegister.INJECTOR);

        registration.addRecipeCatalysts(ENGRAVING, LucenticsBlockRegister.ENGRAVING_TABLE);
        registration.addRecipeCatalysts(ENGRAVING, LucenticsBlockRegister.PRISM_RITUAL);
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
