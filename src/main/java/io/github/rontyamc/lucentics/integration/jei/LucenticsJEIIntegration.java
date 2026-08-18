package io.github.rontyamc.lucentics.integration.jei;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableBlock;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableRecipe;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorRecipe;
import io.github.rontyamc.lucentics.integration.jei.recipe_categories.EngravingRecipeCategory;
import io.github.rontyamc.lucentics.integration.jei.recipe_categories.InjectingRecipeCategory;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class LucenticsJEIIntegration implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID =
            ResourceLocation.fromNamespaceAndPath(Lucentics.MOD_ID, "jei_plugin");

    private static IJeiRuntime runtime;
    private static List<InjectorRecipe> lastInjectorRecipes = new ArrayList<>();
    private static List<EngravingTableRecipe> lastEngravingRecipes = new ArrayList<>();

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new InjectingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new EngravingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(LucenticsBlockRegister.INJECTOR.asItem()), InjectingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(LucenticsBlockRegister.ENGRAVING_TABLE.asItem()), EngravingRecipeCategory.TYPE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        refreshRecipes();
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    public static void refreshRecipes() {
        if (runtime == null) return;

        var level = Minecraft.getInstance().level;
        if (level == null) return;

        List<InjectorRecipe> injRecipes = level.getRecipeManager()
                .getAllRecipesFor(LucenticsRecipeTypesRegister.INJECTION_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();

        if (!lastInjectorRecipes.isEmpty()) {
            runtime.getRecipeManager().hideRecipes(InjectingRecipeCategory.TYPE, lastInjectorRecipes);
        }
        runtime.getRecipeManager().addRecipes(InjectingRecipeCategory.TYPE, injRecipes);
        lastInjectorRecipes = injRecipes;

        List<EngravingTableRecipe> engRecipes = level.getRecipeManager()
                .getAllRecipesFor(LucenticsRecipeTypesRegister.ENGRAVING_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();

        if (!lastEngravingRecipes.isEmpty()) {
            runtime.getRecipeManager().hideRecipes(EngravingRecipeCategory.TYPE, lastEngravingRecipes);
        }
        runtime.getRecipeManager().addRecipes(EngravingRecipeCategory.TYPE, engRecipes);
        lastEngravingRecipes = engRecipes;
    }

    @EventBusSubscriber(modid = Lucentics.MOD_ID, value = Dist.CLIENT)
    public static class JeiRecipeRefreshListener {
        @SubscribeEvent
        public static void onRecipesUpdated(RecipesUpdatedEvent event) {
            refreshRecipes();
        }
    }
}
