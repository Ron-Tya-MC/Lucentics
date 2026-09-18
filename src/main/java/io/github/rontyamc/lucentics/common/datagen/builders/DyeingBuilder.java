package io.github.rontyamc.lucentics.common.datagen.builders;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.recipes.dyeing.DyeingRecipe;
import io.github.rontyamc.lucentics.recipes.dyeing.DyeingRecipeArguments;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class DyeingBuilder implements RecipeBuilder {
    Ingredient input;
    int liquidAmount;
    Colors color;
    ItemStack output;
    protected String suffix;

    public DyeingBuilder(Ingredient input, int liquidAmount, Colors color, ItemStack output) {
        this.input = input;
        this.liquidAmount = liquidAmount;
        this.color = color;
        this.output = output;
        this.suffix = "";
    }

    public static DyeingBuilder create(Ingredient input, int liquidAmount, Colors color, ItemStack output) {
        return new DyeingBuilder(input, liquidAmount, color, output);
    }

    public static DyeingBuilder create(Ingredient input, int liquidAmount, ItemStack output) {
        return create(input, liquidAmount, Colors.SUNLIGHT, output);
    }

    public static DyeingBuilder create(Ingredient input, ItemStack output) {
        return create(input, 0, Colors.SUNLIGHT, output);
    }

    public static DyeingBuilder create(ItemStack output) {
        return create(Ingredient.EMPTY, 0, Colors.SUNLIGHT, output);
    }

    public static DyeingBuilder create(ItemLike output) {
        return create(Ingredient.EMPTY, 0, Colors.SUNLIGHT, new ItemStack(output));
    }


    public DyeingBuilder input(Ingredient input) {
        this.input = input;
        return this;
    }

    public DyeingBuilder liquidAmount(int liquidAmount) {
        this.liquidAmount = liquidAmount;
        return this;
    }

    public DyeingBuilder color(Colors color) {
        this.color = color;
        return this;
    }

    @Override
    public DyeingBuilder unlockedBy(String criterionName, Criterion<?> criterion) {
        return this;
    }

    @Override
    public DyeingBuilder group(String groupName) {
        return this;
    }

    public DyeingBuilder suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    @Override
    public Item getResult() {
        return output.getItem();
    }

    @Override
    public void save(RecipeOutput output) {
        ResourceLocation defaultId = RecipeBuilder.getDefaultRecipeId(getResult());
        ResourceLocation id = Lucentics.defaultLocation("dyeing/" + defaultId.getPath() + suffix);
        save(output, id);
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        DyeingRecipeArguments args = new DyeingRecipeArguments(
                input,
                color,
                liquidAmount,
                this.output
        );

        DyeingRecipe recipe = new DyeingRecipe(LucenticsRecipeTypesRegister.DYEING_INFO, args);
        output.accept(id, recipe, null);
    }
}
