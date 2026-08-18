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
    NonNullList<Output> outputs,
    int processingDuration,
    int dayLightCondition
    ) {

    public RecipeArguments() {
        this(Either.left(SizedIngredient.EMPTY), NonNullList.create(), NonNullList.create(), 0, 0);
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

    public record Output(
            Optional<ItemStack> item,
            Optional<FluidStack> fluid
    ) {
        public static final MapCodec<Output> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
                ItemStack.CODEC.optionalFieldOf("item").forGetter(Output::item),
                FluidStack.CODEC.optionalFieldOf("fluid").forGetter(Output::fluid)
        ).apply(ins, Output::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Output> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional), Output::item,
                FluidStack.STREAM_CODEC.apply(ByteBufCodecs::optional), Output::fluid,
                Output::new
        );

        public Either<ItemStack, FluidStack> toEither() {
            return item.<Either<ItemStack, FluidStack>>map(Either::left)
                    .orElseGet(() -> Either.right(fluid.orElse(FluidStack.EMPTY)));
        }
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
            }, list -> list).optionalFieldOf("trail_inputs", NonNullList.create()).forGetter(RecipeArguments::trailInputs),
            Output.CODEC.codec().listOf().xmap(list -> {
                NonNullList<Output> outputs = NonNullList.create();
                outputs.addAll(list);
                return outputs;
            }, list -> list).fieldOf("outputs").forGetter(RecipeArguments::outputs),
            Codec.INT.fieldOf("processing_duration").forGetter(RecipeArguments::processingDuration),
            Codec.INT.optionalFieldOf("daylight_condition", 0).forGetter(RecipeArguments::dayLightCondition)
    ).apply(ins, RecipeArguments::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeArguments> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.either(SizedIngredient.STREAM_CODEC, SizedFluidIngredient.STREAM_CODEC), RecipeArguments::mainInput,
            ByteBufCodecs.collection(size -> NonNullList.create(), TrailInput.STREAM_CODEC), RecipeArguments::trailInputs,
            ByteBufCodecs.collection(size -> NonNullList.create(), Output.STREAM_CODEC), RecipeArguments::outputs,
            ByteBufCodecs.VAR_INT, RecipeArguments::processingDuration,
            ByteBufCodecs.VAR_INT, RecipeArguments::dayLightCondition,
            RecipeArguments::new
    );
}
