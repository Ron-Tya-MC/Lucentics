package io.github.rontyamc.lucentics.common.datagen.providers;

import io.github.rontyamc.lucentics.common.SoundSpec;
import io.github.rontyamc.lucentics.common.datagen.builders.OutputSpec;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister.LucenticsITags;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class CrushingProvider {
    public CrushingProvider() {}

    protected static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.leaveFolder();

        provider.generic(LucenticsItemRegister.DAWNSTONE_DUST)
                .suffix("_with_copper_hammer")
                .crushing(b -> b.input(LucenticsBlockRegister.DAWNSTONE)
                        .tool(LucenticsItemRegister.COPPER_HAMMER)
                        .clickSound(SoundSpec.of(SoundEvents.TUFF_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.TUFF_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(4)
                        .requiredHits(5));
        provider.generic(LucenticsItemRegister.DAWNSTONE_DUST)
                .suffix("_with_iron_hammer")
                .crushing(b -> b.input(LucenticsBlockRegister.DAWNSTONE)
                        .tool(LucenticsItemRegister.IRON_HAMMER)
                        .clickSound(SoundSpec.of(SoundEvents.TUFF_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.TUFF_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(4)
                        .requiredHits(4));
        provider.generic(LucenticsItemRegister.DAWNSTONE_DUST)
                .suffix("_with_diamond_hammer")
                .crushing(b -> b.input(LucenticsBlockRegister.DAWNSTONE)
                        .tool(LucenticsItemRegister.DIAMOND_HAMMER)
                        .clickSound(SoundSpec.of(SoundEvents.TUFF_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.TUFF_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(4)
                        .requiredHits(3));
        provider.generic(LucenticsItemRegister.DAWNSTONE_DUST)
                .suffix("_with_golden_hammer")
                .crushing(b -> b.input(LucenticsBlockRegister.DAWNSTONE)
                        .tool(LucenticsItemRegister.GOLDEN_HAMMER)
                        .clickSound(SoundSpec.of(SoundEvents.TUFF_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.TUFF_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(10)
                        .requiredHits(1));

        provider.generic()
                .crushing(b -> b.input(Blocks.NETHER_WART_BLOCK)
                        .tool(ItemTags.HOES)
                        .output(OutputSpec.of(Items.NETHER_WART).count(UniformInt.of(4,8)))
                        .clickSound(SoundSpec.of(SoundEvents.WART_BLOCK_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.WART_BLOCK_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(1)
                        .requiredHits(3));
        provider.generic()
                .crushing(b -> b.input(Blocks.BRICKS)
                        .tool(ItemTags.PICKAXES)
                        .output(Items.BRICK, 4)
                        .clickSound(SoundSpec.of(SoundEvents.DEEPSLATE_BRICKS_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.DEEPSLATE_BRICKS_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(1)
                        .requiredHits(2));
        provider.generic()
                .crushing(b -> b.input(Blocks.QUARTZ_BLOCK)
                        .tool(ItemTags.PICKAXES)
                        .output(Items.QUARTZ, 4)
                        .clickSound(SoundSpec.of(SoundEvents.STONE_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.STONE_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(1)
                        .requiredHits(2));

        provider.generic()
                .path("green_dye")
                .suffix("_from_short_grass")
                .crushing(b -> b.input(Blocks.SHORT_GRASS)
                        .tool(LucenticsITags.HAMMERS.tag)
                        .output(OutputSpec.of(Items.WHEAT_SEEDS).probability(0.3f))
                        .output(OutputSpec.of(Items.GREEN_DYE).probability(0.03f))
                        .clickSound(SoundSpec.of(SoundEvents.GRASS_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.GRASS_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(1)
                        .requiredHits(1));
        provider.generic()
                .path("green_dye")
                .suffix("_from_tall_grass")
                .crushing(b -> b.input(Blocks.TALL_GRASS)
                        .tool(LucenticsITags.HAMMERS.tag)
                        .output(OutputSpec.of(Items.WHEAT_SEEDS).count(UniformInt.of(1,2)).probability(0.3f))
                        .output(OutputSpec.of(Items.GREEN_DYE).probability(0.07f))
                        .clickSound(SoundSpec.of(SoundEvents.GRASS_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.GRASS_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(1)
                        .requiredHits(1));
        provider.generic()
                .path("green_dye")
                .suffix("_from_fern")
                .crushing(b -> b.input(Blocks.FERN)
                        .tool(LucenticsITags.HAMMERS.tag)
                        .output(OutputSpec.of(Items.WHEAT_SEEDS).probability(0.1f))
                        .output(OutputSpec.of(Items.GREEN_DYE).probability(0.07f))
                        .clickSound(SoundSpec.of(SoundEvents.GRASS_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.GRASS_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(1)
                        .requiredHits(1));
        provider.generic()
                .path("green_dye")
                .suffix("_from_large_fern")
                .crushing(b -> b.input(Blocks.LARGE_FERN)
                        .tool(LucenticsITags.HAMMERS.tag)
                        .output(OutputSpec.of(Items.WHEAT_SEEDS).count(UniformInt.of(1,2)).probability(0.1f))
                        .output(OutputSpec.of(Items.GREEN_DYE).probability(0.15f))
                        .clickSound(SoundSpec.of(SoundEvents.GRASS_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.GRASS_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(1)
                        .requiredHits(1));
    }
}
