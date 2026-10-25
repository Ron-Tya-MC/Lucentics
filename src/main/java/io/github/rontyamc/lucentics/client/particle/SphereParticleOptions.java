package io.github.rontyamc.lucentics.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rontyamc.lucentics.registers.LucenticsParticleRegister;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SphereParticleOptions(float red, float green, float blue) implements ParticleOptions {
    public static final MapCodec<SphereParticleOptions> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.FLOAT.fieldOf("red").forGetter(SphereParticleOptions::red),
            Codec.FLOAT.fieldOf("green").forGetter(SphereParticleOptions::green),
            Codec.FLOAT.fieldOf("blue").forGetter(SphereParticleOptions::blue)
    ).apply(ins, SphereParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SphereParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SphereParticleOptions::red,
            ByteBufCodecs.FLOAT, SphereParticleOptions::green,
            ByteBufCodecs.FLOAT, SphereParticleOptions::blue,
            SphereParticleOptions::new
    );

    @Override
    public ParticleType<SphereParticleOptions> getType() {
        return LucenticsParticleRegister.SPHERE.get();
    }
}
