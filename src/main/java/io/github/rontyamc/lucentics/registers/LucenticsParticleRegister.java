package io.github.rontyamc.lucentics.registers;

import com.mojang.serialization.MapCodec;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.client.particle.GlowParticleOptions;
import io.github.rontyamc.lucentics.client.particle.SphereParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LucenticsParticleRegister {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Lucentics.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<GlowParticleOptions>> GLOW =
            PARTICLE_TYPES.register("glow", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<GlowParticleOptions> codec() {
                    return GlowParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, GlowParticleOptions> streamCodec() {
                    return GlowParticleOptions.STREAM_CODEC;
                }
            });

    public static final DeferredHolder<ParticleType<?>, ParticleType<SphereParticleOptions>> SPHERE =
            PARTICLE_TYPES.register("sphere", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<SphereParticleOptions> codec() {
                    return SphereParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, SphereParticleOptions> streamCodec() {
                    return SphereParticleOptions.STREAM_CODEC;
                }
            });

    public static void register(IEventBus bus) {
        PARTICLE_TYPES.register(bus);
    }
}
