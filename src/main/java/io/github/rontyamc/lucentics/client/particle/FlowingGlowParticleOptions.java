package io.github.rontyamc.lucentics.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rontyamc.lucentics.common.util.MiscUtil;
import io.github.rontyamc.lucentics.registers.LucenticsParticleRegister;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record FlowingGlowParticleOptions(NonNullList<Vec3> waypoints, float red, float blue, float green, int duration, float scale, Vec3 delta) implements ParticleOptions {
    public static final MapCodec<FlowingGlowParticleOptions> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Vec3.CODEC.listOf().xmap(list -> {
                NonNullList<Vec3> waypoints = NonNullList.create();
                waypoints.addAll(list);
                return waypoints;
            }, list -> list).fieldOf("waypoints").forGetter(FlowingGlowParticleOptions::waypoints),
            Codec.FLOAT.fieldOf("red").forGetter(FlowingGlowParticleOptions::red),
            Codec.FLOAT.fieldOf("blue").forGetter(FlowingGlowParticleOptions::blue),
            Codec.FLOAT.fieldOf("green").forGetter(FlowingGlowParticleOptions::green),
            Codec.INT.fieldOf("duration").forGetter(FlowingGlowParticleOptions::duration),
            Codec.FLOAT.fieldOf("scale").forGetter(FlowingGlowParticleOptions::scale),
            Vec3.CODEC.fieldOf("delta").forGetter(FlowingGlowParticleOptions::delta)
            ).apply(ins, FlowingGlowParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlowingGlowParticleOptions> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            ByteBufCodecs.collection(size -> NonNullList.create(), MiscUtil.VEC3_STREAM_CODEC), FlowingGlowParticleOptions::waypoints,
            ByteBufCodecs.FLOAT, FlowingGlowParticleOptions::red,
            ByteBufCodecs.FLOAT, FlowingGlowParticleOptions::blue,
            ByteBufCodecs.FLOAT, FlowingGlowParticleOptions::green,
            ByteBufCodecs.VAR_INT, FlowingGlowParticleOptions::duration,
            ByteBufCodecs.FLOAT, FlowingGlowParticleOptions::scale,
            MiscUtil.VEC3_STREAM_CODEC, FlowingGlowParticleOptions::delta,
            FlowingGlowParticleOptions::new
    );

    @Override
    public ParticleType<FlowingGlowParticleOptions> getType() {
        return LucenticsParticleRegister.FLOWING_GLOW.get();
    }
}
