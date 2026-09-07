package io.github.rontyamc.lucentics.common.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.util.MiscUtilities;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public record RecipeArguments(
    Either<SizedIngredient, SizedFluidIngredient> mainInput,
    NonNullList<TrailInput> trailInputs,
    NonNullList<List<WeightedOutput>> outputs,
    int processingDuration,
    int dayLightCondition
    ) {

    public RecipeArguments() {
        this(Either.left(SizedIngredient.EMPTY), NonNullList.create(), NonNullList.create(), 0, 0);
    }

    public static final MapCodec<RecipeArguments> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.either(SizedIngredient.CODEC, SizedFluidIngredient.FLAT_CODEC).fieldOf("input").forGetter(RecipeArguments::mainInput),
            TrailInput.CODEC.codec().listOf().xmap(list -> {
                NonNullList<TrailInput> inputs = NonNullList.create();
                inputs.addAll(list);
                return inputs;
            }, list -> list).optionalFieldOf("trail_inputs", NonNullList.create()).forGetter(RecipeArguments::trailInputs),
            MiscUtilities.singleOrList(WeightedOutput.CODEC.codec()).listOf().xmap(list -> {
                NonNullList<List<WeightedOutput>> groups = NonNullList.create();
                groups.addAll(list);
                return groups;
            }, list -> list).optionalFieldOf("outputs", NonNullList.create()).forGetter(RecipeArguments::outputs),
            Codec.INT.fieldOf("processing_duration").forGetter(RecipeArguments::processingDuration),
            Codec.INT.optionalFieldOf("daylight_condition", 0).forGetter(RecipeArguments::dayLightCondition)
    ).apply(ins, RecipeArguments::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeArguments> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.either(SizedIngredient.STREAM_CODEC, SizedFluidIngredient.STREAM_CODEC), RecipeArguments::mainInput,
            ByteBufCodecs.collection(size -> NonNullList.create(), TrailInput.STREAM_CODEC), RecipeArguments::trailInputs,
            ByteBufCodecs.collection(size -> NonNullList.create(), ByteBufCodecs.collection(size -> NonNullList.create(), WeightedOutput.STREAM_CODEC)), RecipeArguments::outputs,
            ByteBufCodecs.VAR_INT, RecipeArguments::processingDuration,
            ByteBufCodecs.VAR_INT, RecipeArguments::dayLightCondition,
            RecipeArguments::new
    );

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

    public record WeightedOutput(
            Either<ItemOutput, FluidOutput> content,
            float probability,
            int weight
    ) {
        public record ItemOutput(ItemStack stack, IntProvider count) {
            public static final MapCodec<ItemOutput> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
                    ItemStack.CODEC.fieldOf("item").forGetter(ItemOutput::stack),
                    IntProvider.CODEC.optionalFieldOf("count", ConstantInt.of(1)).forGetter(ItemOutput::count)
            ).apply(ins, ItemOutput::new));

            public static final StreamCodec<RegistryFriendlyByteBuf, ItemOutput> STREAM_CODEC = StreamCodec.composite(
                    ItemStack.STREAM_CODEC, ItemOutput::stack,
                    ByteBufCodecs.fromCodecWithRegistries(IntProvider.CODEC), ItemOutput::count,
                    ItemOutput::new
            );

            public ItemStack roll(RandomSource random) {
                return stack.copyWithCount(count.sample(random));
            }
        }

        public record FluidOutput(FluidStack stack, IntProvider amount) {
            public static final MapCodec<FluidOutput> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
                    FluidStack.CODEC.fieldOf("fluid").forGetter(FluidOutput::stack),
                    IntProvider.CODEC.optionalFieldOf("amount", ConstantInt.of(FluidType.BUCKET_VOLUME)).forGetter(FluidOutput::amount)
            ).apply(ins, FluidOutput::new));

            public static final StreamCodec<RegistryFriendlyByteBuf, FluidOutput> STREAM_CODEC = StreamCodec.composite(
                    FluidStack.STREAM_CODEC, FluidOutput::stack,
                    ByteBufCodecs.fromCodecWithRegistries(IntProvider.CODEC), FluidOutput::amount,
                    FluidOutput::new
            );

            public FluidStack roll(RandomSource random) {
                return stack.copyWithAmount(amount.sample(random));
            }
        }

        public static final MapCodec<WeightedOutput> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
                Codec.either(ItemOutput.CODEC.codec(), FluidOutput.CODEC.codec()).fieldOf("content").forGetter(WeightedOutput::content),
                Codec.FLOAT.optionalFieldOf("probability", 1.0f).forGetter(WeightedOutput::probability),
                Codec.INT.optionalFieldOf("weight", 1).forGetter(WeightedOutput::weight)
        ).apply(ins, WeightedOutput::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WeightedOutput> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.either(ItemOutput.STREAM_CODEC, FluidOutput.STREAM_CODEC), WeightedOutput::content,
                ByteBufCodecs.FLOAT, WeightedOutput::probability,
                ByteBufCodecs.VAR_INT, WeightedOutput::weight,
                WeightedOutput::new
        );

        public ItemStack getItemStack() {
            AtomicReference<ItemStack> stack = new AtomicReference<>(ItemStack.EMPTY);
            this.content.left().ifPresent(content -> stack.set(content.stack()));
            return stack.get();
        }

        public FluidStack getFluidStack() {
            AtomicReference<FluidStack> stack = new AtomicReference<>(FluidStack.EMPTY);
            this.content.right().ifPresent(content -> stack.set(content.stack()));
            return stack.get();
        }
    }
}
