package io.github.rontyamc.lucentics.recipes.crushing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rontyamc.lucentics.common.SoundSpec;
import io.github.rontyamc.lucentics.common.recipe.RecipeArguments.WeightedOutput;
import io.github.rontyamc.lucentics.common.util.MiscUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;
import java.util.Optional;

public record CrushingRecipeArguments(
    HolderSet<Block> block,
    Ingredient tool,
    int requiredHits,
    int damagePerHit,
    Optional<SoundSpec> clickSound,
    Optional<SoundSpec> breakSound,
    NonNullList<List<WeightedOutput>> outputs
    ) {

    public CrushingRecipeArguments() {
        this(HolderSet.direct(), Ingredient.EMPTY, 0, 0, Optional.empty(), Optional.empty(), NonNullList.create());
    }

    public static final MapCodec<CrushingRecipeArguments> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("input").forGetter(CrushingRecipeArguments::block),
            Ingredient.CODEC.fieldOf("tool").forGetter(CrushingRecipeArguments::tool),
            Codec.INT.optionalFieldOf("required_hit", 1).forGetter(CrushingRecipeArguments::requiredHits),
            Codec.INT.optionalFieldOf("damage_per_hit", 1).forGetter(CrushingRecipeArguments::damagePerHit),
            SoundSpec.CODEC.optionalFieldOf("click_sound").forGetter(CrushingRecipeArguments::clickSound),
            SoundSpec.CODEC.optionalFieldOf("break_sound").forGetter(CrushingRecipeArguments::breakSound),
            MiscUtil.singleOrList(WeightedOutput.CODEC.codec()).listOf().xmap(list -> {
                NonNullList<List<WeightedOutput>> groups = NonNullList.create();
                groups.addAll(list);
                return groups;
            }, list -> list).optionalFieldOf("outputs", NonNullList.create()).forGetter(CrushingRecipeArguments::outputs)
    ).apply(ins, CrushingRecipeArguments::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrushingRecipeArguments> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            ByteBufCodecs.holderSet(Registries.BLOCK), CrushingRecipeArguments::block,
            Ingredient.CONTENTS_STREAM_CODEC, CrushingRecipeArguments::tool,
            ByteBufCodecs.VAR_INT, CrushingRecipeArguments::requiredHits,
            ByteBufCodecs.VAR_INT, CrushingRecipeArguments::damagePerHit,
            SoundSpec.STREAM_CODEC.apply(ByteBufCodecs::optional), CrushingRecipeArguments::clickSound,
            SoundSpec.STREAM_CODEC.apply(ByteBufCodecs::optional), CrushingRecipeArguments::breakSound,
            ByteBufCodecs.collection(size -> NonNullList.create(), ByteBufCodecs.collection(size -> NonNullList.create(), WeightedOutput.STREAM_CODEC)), CrushingRecipeArguments::outputs,
            CrushingRecipeArguments::new
    );
}
