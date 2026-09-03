package io.github.rontyamc.lucentics.recipes.crushing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Optional;

public record CrushingRecipeArguments(
    HolderSet<Block> block,
    Ingredient tool,
    int requiredHits,
    int damagePerHit,
    Optional<Holder<SoundEvent>> clickSound,
    Optional<Holder<SoundEvent>> breakSound,
    NonNullList<RecipeArguments.Output> outputs
    ) {

    public CrushingRecipeArguments() {
        this(HolderSet.direct(), Ingredient.EMPTY, 0, 0, Optional.empty(), Optional.empty(), NonNullList.create());
    }

    public static final MapCodec<CrushingRecipeArguments> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("input").forGetter(CrushingRecipeArguments::block),
            Ingredient.CODEC.fieldOf("tool").forGetter(CrushingRecipeArguments::tool),
            Codec.INT.optionalFieldOf("required_hit", 1).forGetter(CrushingRecipeArguments::requiredHits),
            Codec.INT.optionalFieldOf("damage_per_hit", 1).forGetter(CrushingRecipeArguments::damagePerHit),
            SoundEvent.CODEC.optionalFieldOf("click_sound").forGetter(CrushingRecipeArguments::clickSound),
            SoundEvent.CODEC.optionalFieldOf("break_sound").forGetter(CrushingRecipeArguments::breakSound),
            RecipeArguments.Output.CODEC.codec().listOf().xmap(list -> {
                NonNullList<RecipeArguments.Output> out = NonNullList.create();
                out.addAll(list);
                return out;
            }, list -> list).fieldOf("outputs").forGetter(CrushingRecipeArguments::outputs)
    ).apply(ins, CrushingRecipeArguments::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrushingRecipeArguments> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            ByteBufCodecs.holderSet(Registries.BLOCK), CrushingRecipeArguments::block,
            Ingredient.CONTENTS_STREAM_CODEC, CrushingRecipeArguments::tool,
            ByteBufCodecs.VAR_INT, CrushingRecipeArguments::requiredHits,
            ByteBufCodecs.VAR_INT, CrushingRecipeArguments::damagePerHit,
            ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).apply(ByteBufCodecs::optional), CrushingRecipeArguments::clickSound,
            ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).apply(ByteBufCodecs::optional), CrushingRecipeArguments::breakSound,
            ByteBufCodecs.collection(size -> NonNullList.create(), RecipeArguments.Output.STREAM_CODEC), CrushingRecipeArguments::outputs,
            CrushingRecipeArguments::new
    );
}
