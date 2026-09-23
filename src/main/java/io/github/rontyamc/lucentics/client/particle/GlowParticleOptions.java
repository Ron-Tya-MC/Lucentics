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
import net.minecraft.util.RandomSource;

public record GlowParticleOptions(float red, float green, float blue, float size) implements ParticleOptions {
    private static final RandomSource RANDOM_SOURCE = RandomSource.create();

    public static final MapCodec<GlowParticleOptions> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.FLOAT.fieldOf("red").forGetter(GlowParticleOptions::red),
            Codec.FLOAT.fieldOf("green").forGetter(GlowParticleOptions::green),
            Codec.FLOAT.fieldOf("blue").forGetter(GlowParticleOptions::blue),
            Codec.FLOAT.fieldOf("size").forGetter(GlowParticleOptions::size)
    ).apply(ins, GlowParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GlowParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, GlowParticleOptions::red,
            ByteBufCodecs.FLOAT, GlowParticleOptions::green,
            ByteBufCodecs.FLOAT, GlowParticleOptions::blue,
            ByteBufCodecs.FLOAT, GlowParticleOptions::size,
            GlowParticleOptions::new
    );

    public GlowParticleOptions(float red, float green, float blue) {
        this(red, green, blue, RANDOM_SOURCE.nextFloat() * 0.1f + 0.05f);
    }

    @Override
    public ParticleType<GlowParticleOptions> getType() {
        return LucenticsParticleRegister.GLOW.get();
    }
}
