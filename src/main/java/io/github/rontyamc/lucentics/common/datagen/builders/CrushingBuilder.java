package io.github.rontyamc.lucentics.common.datagen.builders;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.SoundSpec;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingRecipe;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingRecipeArguments;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CrushingBuilder implements RecipeBuilder {
    private HolderSet<Block> block;
    private final List<Holder<Block>> blocks = new ArrayList<>();
    private Ingredient tool;
    private int requiredHits = 1;
    private int damagePerHit = 1;
    private Optional<SoundSpec> clickSound;
    private Optional<SoundSpec> breakSound;
    private final NonNullList<RecipeArguments.Output> outputs = NonNullList.create();
    protected String suffix;

    public CrushingBuilder(HolderSet<Block> block, Ingredient tool, RecipeArguments.Output output) {
        this.block = block;
        this.tool = tool;
        this.outputs.add(output);
        this.suffix = "";
    }

    public static CrushingBuilder create(HolderSet<Block> block, Ingredient tool, RecipeArguments.Output output) {
        return new CrushingBuilder(block, tool, output);
    }

    public static CrushingBuilder create(HolderSet<Block> block, Ingredient tool, ItemStack result) {
        return create(block, tool, new RecipeArguments.Output(Optional.of(result), Optional.empty()));
    }
    public static CrushingBuilder create(HolderSet<Block> block, Ingredient tool, ItemLike result, int count) {
        return create(block, tool, new ItemStack(result, count));
    }

    public static CrushingBuilder create(HolderSet<Block> block, Ingredient tool, ItemLike result) {
        return create(block, tool, result, 1);
    }

    public static CrushingBuilder create(HolderSet<Block> block, Ingredient tool) {
        return create(block, tool, ItemStack.EMPTY);
    }

    public static CrushingBuilder create(Ingredient tool, ItemLike result) {
        return create(HolderSet.empty(), tool, new ItemStack(result));
    }

    public CrushingBuilder input(Holder<Block> block) {
        this.blocks.add(block);
        return this;
    }

    @SuppressWarnings("deprecation")
    public CrushingBuilder input(Block block) {
        this.blocks.add(block.builtInRegistryHolder());
        return this;
    }

    public CrushingBuilder tool(Ingredient tool) {
        this.tool = tool;
        return this;
    }

    public CrushingBuilder tool(ItemLike tool) {
        return tool(Ingredient.of(tool));
    }

    public CrushingBuilder requiredHits(int hits) {
        this.requiredHits = hits;
        return this;
    }

    public CrushingBuilder damagePerHit(int damage) {
        this.damagePerHit = damage;
        return this;
    }

    public CrushingBuilder clickSound(SoundSpec sound) {
        this.clickSound = Optional.of(sound);
        return this;
    }

    public CrushingBuilder clickSound(Holder<SoundEvent> sound) {
        this.clickSound = Optional.of(SoundSpec.of(sound));
        return this;
    }

    public CrushingBuilder breakSound(SoundSpec sound) {
        this.breakSound = Optional.of(sound);
        return this;
    }

    public CrushingBuilder breakSound(Holder<SoundEvent> sound) {
        this.breakSound = Optional.of(SoundSpec.of(sound));
        return this;
    }

    public CrushingBuilder output(RecipeArguments.Output output) {
        this.outputs.add(output);
        return this;
    }

    public CrushingBuilder output(ItemStack output) {
        return output(new RecipeArguments.Output(Optional.of(output), Optional.empty()));
    }

    public CrushingBuilder output(ItemLike output, int count) {
        return output(new ItemStack(output, count));
    }

    public CrushingBuilder output(ItemLike output) {
        return output(new ItemStack(output));
    }

    @Override
    public CrushingBuilder unlockedBy(String criterionName, Criterion<?> criterion) {
        return this;
    }

    @Override
    public CrushingBuilder group(String groupName) {
        return this;
    }

    public CrushingBuilder suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    @Override
    public Item getResult() {
        AtomicReference<Item> result = new AtomicReference<>();
        outputs.getFirst().item().ifPresent(item -> result.set(item.getItem()));
        return result.get();
    }

    @Override
    public void save(RecipeOutput output) {
        ResourceLocation defaultId = RecipeBuilder.getDefaultRecipeId(getResult());
        ResourceLocation id = Lucentics.defaultLocation("crushing/" + defaultId.getPath() + suffix);
        save(output, id);
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        List<Holder<Block>> allBlocks = new ArrayList<>();
        block.forEach(allBlocks::add);
        allBlocks.addAll(blocks);
        HolderSet<Block> blockSet = HolderSet.direct(allBlocks);

        CrushingRecipeArguments args = new CrushingRecipeArguments(
                blockSet,
                tool,
                requiredHits,
                damagePerHit,
                clickSound,
                breakSound,
                outputs
        );

        CrushingRecipe recipe = new CrushingRecipe(LucenticsRecipeTypesRegister.CRUSHING_INFO, args);
        output.accept(id, recipe, null);
    }
}
