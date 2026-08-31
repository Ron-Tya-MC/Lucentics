package io.github.rontyamc.lucentics.common.datagen.builders;

import com.mojang.datafixers.util.Either;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableRecipe;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.common.recipe.SizedIngredient;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

public class EngravingBuilder implements RecipeBuilder {
    private SizedIngredient input;
    private final ItemStack output;
    private final NonNullList<RecipeArguments.TrailInput> trails = NonNullList.create();
    protected String suffix;
    private int processingDuration = 100;
    private int daylightCondition = 0;

    protected EngravingBuilder(SizedIngredient input, ItemStack output) {
        this.input = input;
        this.output = output;
        this.suffix = "";
    }

    public static EngravingBuilder create(SizedIngredient input, ItemStack output) {
        return new EngravingBuilder(input, output);
    }

    public static EngravingBuilder create(SizedIngredient input, ItemLike result, int count) {
        return new EngravingBuilder(input, new ItemStack(result, count));
    }

    public static EngravingBuilder create(SizedIngredient input, ItemLike result) {
        return new EngravingBuilder(input, new ItemStack(result, 1));
    }

    public EngravingBuilder input(SizedIngredient input) {
        this.input = input;
        return this;
    }

    public EngravingBuilder input(ItemLike input, int count) {
        return input(SizedIngredient.of(input, count));
    }

    public EngravingBuilder input(ItemLike input) {
        return input(input, 1);
    }

    public EngravingBuilder trail(TrailBuilder trailBuilder) {
        trails.add(trailBuilder.build());
        return this;
    }

    public EngravingBuilder duration(int ticks) {
        this.processingDuration = ticks;
        return this;
    }

    public EngravingBuilder daylight(int level) {
        this.daylightCondition = level;
        return this;
    }

    @Override
    public EngravingBuilder unlockedBy(String criterionName, Criterion<?> criterion) {
        return this;
    }

    @Override
    public EngravingBuilder group(String groupName) {
        return this;
    }

    public EngravingBuilder suffix(String suffix) {
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
        ResourceLocation id = Lucentics.defaultLocation("engraving/" + defaultId.getPath() + suffix);
        save(output, id);
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        NonNullList<RecipeArguments.Output> outputs = NonNullList.create();
        outputs.add(new RecipeArguments.Output(Optional.of(this.output), Optional.empty()));

        RecipeArguments args = new RecipeArguments(
                Either.left(input),
                trails,
                outputs,
                processingDuration,
                daylightCondition
        );

        EngravingTableRecipe recipe = new EngravingTableRecipe(LucenticsRecipeTypesRegister.ENGRAVING_INFO, args);
        output.accept(id, recipe, null);
    }
}
