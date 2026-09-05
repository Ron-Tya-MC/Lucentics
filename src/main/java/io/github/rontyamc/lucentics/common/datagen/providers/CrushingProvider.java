package io.github.rontyamc.lucentics.common.datagen.providers;

import io.github.rontyamc.lucentics.common.SoundSpec;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class CrushingProvider {
    public CrushingProvider() {}

    protected static void buildRecipes(LucenticsRecipeProvider provider, RecipeOutput recipeOutput) {
        provider.leaveFolder();

        provider.generic(LucenticsItemRegister.DAWNSTONE_DUST).unlockedBy(LucenticsItemRegister.NOTHING)
                .suffix("_with_copper_hammer")
                .crushing(b -> b.input(LucenticsBlockRegister.DAWNSTONE)
                        .tool(LucenticsItemRegister.COPPER_HAMMER)
                        .clickSound(SoundSpec.of(SoundEvents.TUFF_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.TUFF_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(4)
                        .requiredHits(5));
        provider.generic(LucenticsItemRegister.DAWNSTONE_DUST).unlockedBy(LucenticsItemRegister.NOTHING)
                .suffix("_with_iron_hammer")
                .crushing(b -> b.input(LucenticsBlockRegister.DAWNSTONE)
                        .tool(LucenticsItemRegister.IRON_HAMMER)
                        .clickSound(SoundSpec.of(SoundEvents.TUFF_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.TUFF_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(4)
                        .requiredHits(4));
        provider.generic(LucenticsItemRegister.DAWNSTONE_DUST).unlockedBy(LucenticsItemRegister.NOTHING)
                .suffix("_with_diamond_hammer")
                .crushing(b -> b.input(LucenticsBlockRegister.DAWNSTONE)
                        .tool(LucenticsItemRegister.DIAMOND_HAMMER)
                        .clickSound(SoundSpec.of(SoundEvents.TUFF_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.TUFF_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(4)
                        .requiredHits(3));
        provider.generic(LucenticsItemRegister.DAWNSTONE_DUST).unlockedBy(LucenticsItemRegister.NOTHING)
                .suffix("_with_golden_hammer")
                .crushing(b -> b.input(LucenticsBlockRegister.DAWNSTONE)
                        .tool(LucenticsItemRegister.GOLDEN_HAMMER)
                        .clickSound(SoundSpec.of(SoundEvents.TUFF_HIT, SoundSource.BLOCKS))
                        .breakSound(SoundSpec.of(SoundEvents.TUFF_BREAK, SoundSource.BLOCKS))
                        .damagePerHit(10)
                        .requiredHits(1));
    }
}
