package io.github.rontyamc.lucentics.common.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rontyamc.lucentics.Lucentics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class BehaviorType<T extends BlockEntityBehavior> {
    private final ResourceLocation id;
    public BehaviorType(ResourceLocation id) {
        this.id = id;
    }

    public BehaviorType(String name) {
        int i = name.indexOf(":");
        if  (i >= 0) this.id = ResourceLocation.tryParse(name);
        else this.id = ResourceLocation.tryParse(Lucentics.MOD_ID + ":" + name);
    }

    public static final MapCodec<BehaviorType> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(BehaviorType::getId)
    ).apply(ins, BehaviorType::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BehaviorType> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, BehaviorType::getId,
            BehaviorType::new
    );

    public ResourceLocation getId() {
        return this.id;
    }
}
