package io.github.rontyamc.lucentics.common.datagen.builders;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.SoundSpec;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments.WeightedOutput;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingRecipe;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingRecipeArguments;
import io.github.rontyamc.lucentics.registers.LucenticsRecipeTypesRegister;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CrushingBuilder implements RecipeBuilder, IdPathResolvable {
    private HolderSet<Block> block;
    private final List<Holder<Block>> blocks = new ArrayList<>();
    private Ingredient tool;
    private int requiredHits = 1;
    private int damagePerHit = 1;
    private Optional<SoundSpec> clickSound;
    private Optional<SoundSpec> breakSound;
    private ItemStack primaryOutput;
    private final NonNullList<List<WeightedOutput>> outputGroups = NonNullList.create();
    protected String suffix;

    public CrushingBuilder(HolderSet<Block> block, Ingredient tool, ItemStack output) {
        this.block = block;
        this.tool = tool;
        this.primaryOutput = output;
        if (!output.isEmpty()) this.outputGroups.add(List.of(OutputSpec.of(output).build()));
        this.suffix = "";
    }

    public static CrushingBuilder create(HolderSet<Block> block, Ingredient tool, ItemStack result) {
        return new CrushingBuilder(block, tool, result);
    }

    public static CrushingBuilder create(HolderSet<Block> block, Ingredient tool, ItemLike result, int count) {
        return create(block, tool, new ItemStack(result, count));
    }

    public static CrushingBuilder create(HolderSet<Block> block, Ingredient tool, ItemLike result) {
        return create(block, tool, new ItemStack(result, 1));
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

    public CrushingBuilder tool(TagKey<Item> tool) {
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

    public CrushingBuilder output(OutputSpec spec) {
        outputGroups.add(List.of(spec.build()));
        return this;
    }

    public CrushingBuilder outputGroup(OutputSpec... specs) {
        outputGroups.add(Arrays.stream(specs).map(OutputSpec::build).toList());
        return this;
    }

    public CrushingBuilder output(ItemLike item) {
        return output(OutputSpec.of(item));
    }

    public CrushingBuilder output(ItemLike item, int count) {
        return output(OutputSpec.of(item).amount(count));
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
        return primaryOutput.getItem();
    }

    @Override
    public String resolveIdPath() {
        if (!primaryOutput.isEmpty()) {
            return BuiltInRegistries.ITEM.getKey(primaryOutput.getItem()).getPath();
        }
        for (List<WeightedOutput> group : outputGroups) {
            for (WeightedOutput candidate : group) {
                Optional<String> path = candidate.content().left()
                        .filter(item -> !item.stack().isEmpty())
                        .map(item -> BuiltInRegistries.ITEM.getKey(item.stack().getItem()).getPath())
                        .or(() -> candidate.content().right()
                                .map(fluid -> BuiltInRegistries.FLUID.getKey(fluid.stack().getFluid()).getPath()));
                if (path.isPresent()) return path.get();
            }
        }
        Lucentics.LOGGER.warn(
                "Failed to resolve ID path; falling back to \"unknown\". You MUST include a non-empty item/fluid output or specify a path using path(String path) to prevent recipe collisions."
        );
        return "unknown";
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

        NonNullList<List<WeightedOutput>> outputs = NonNullList.create();
        outputs.addAll(outputGroups);

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
