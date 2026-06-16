package io.github.rontyamc.lucentics.common.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Optional;

public record RecipeArguments (
    Either<SizedIngredient, SizedFluidIngredient> mainInput,
    NonNullList<TrailInput> trailInputs,
    ItemStack output,
    FluidStack outputFluid,
    int processingDuration,
    Optional<Integer> dayLightCondition
    ) {

    public RecipeArguments() {
        this(Either.left(SizedIngredient.EMPTY), NonNullList.create(), ItemStack.EMPTY, FluidStack.EMPTY, 0, Optional.of(0));
    }

    public record TrailInput(
            String color,
            NonNullList<OrderingInput> inputs
    ) {
        public static final MapCodec<TrailInput> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
                Codec.STRING.fieldOf("color").forGetter(TrailInput::color),
                OrderingInput.CODEC.codec().listOf().xmap(list -> {
                    NonNullList<OrderingInput> inputs = NonNullList.create();
                    inputs.addAll(list);
                    return inputs;
                }, list -> list).fieldOf("inputs").forGetter(TrailInput::inputs)
        ).apply(ins, TrailInput::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TrailInput> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, TrailInput::color,
                ByteBufCodecs.collection(size -> NonNullList.create(), OrderingInput.STREAM_CODEC), TrailInput::inputs,
                TrailInput::new
        );
    }

    public record OrderingInput(
            Either<SizedIngredient,SizedFluidIngredient> ingredient,
            Optional<BehaviorType> requiredType,
            boolean notConsume
    ) {
        public static final MapCodec<OrderingInput> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
                Codec.either(SizedIngredient.CODEC, SizedFluidIngredient.FLAT_CODEC).fieldOf("input").forGetter(OrderingInput::ingredient),
                BehaviorType.CODEC.codec().optionalFieldOf("required_type").forGetter(OrderingInput::requiredType),
                Codec.BOOL.optionalFieldOf("not_consume",false).forGetter(OrderingInput::notConsume)
        ).apply(ins, OrderingInput::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, OrderingInput> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.either(SizedIngredient.STREAM_CODEC, SizedFluidIngredient.STREAM_CODEC), OrderingInput::ingredient,
                BehaviorType.STREAM_CODEC.apply(ByteBufCodecs::optional), OrderingInput::requiredType,
                ByteBufCodecs.BOOL, OrderingInput::notConsume,
                OrderingInput::new
        );
    }

    public static final MapCodec<RecipeArguments> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.either(SizedIngredient.CODEC, SizedFluidIngredient.FLAT_CODEC).fieldOf("input").forGetter(RecipeArguments::mainInput),
            TrailInput.CODEC.codec().listOf().xmap(list -> {
                NonNullList<TrailInput> inputs = NonNullList.create();
                inputs.addAll(list);
                return inputs;
            }, list -> list).fieldOf("trail_inputs").forGetter(RecipeArguments::trailInputs),
            ItemStack.CODEC.fieldOf("output").forGetter(RecipeArguments::output),
            FluidStack.CODEC.fieldOf("output_fluid").forGetter(RecipeArguments::outputFluid),
            Codec.INT.fieldOf("processing_duration").forGetter(RecipeArguments::processingDuration),
            Codec.INT.optionalFieldOf("daylight_condition").forGetter(RecipeArguments::dayLightCondition)
    ).apply(ins, RecipeArguments::new));
}
