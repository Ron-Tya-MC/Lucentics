package io.github.rontyamc.lucentics.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.Optional;

public record SoundSpec(Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, float minVolume) {
    public static SoundSpec of(Holder<SoundEvent> sound) {
        return new SoundSpec(sound, SoundSource.MASTER, 1.0f, 1.0f, 0.0f);
    }

    public static SoundSpec of(Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, float minVolume) {
        return new SoundSpec(sound, source, volume, pitch, minVolume);
    }
    public static SoundSpec of(Holder<SoundEvent> sound, SoundSource source, float volume, float pitch) {
        return new SoundSpec(sound, source, volume, pitch, 0.0f);
    }

    public static SoundSpec of(Holder<SoundEvent> sound, SoundSource source) {
        return new SoundSpec(sound, source, 1.0f, 1.0f, 0.0f);
    }

    public static SoundSpec of(SoundEvent sound, SoundSource source) {
        return of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source);
    }

    private static final Codec<SoundSource> SOUND_SOURCE_CODEC = Codec.STRING.comapFlatMap(
            name -> bySourceName(name)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown sound source: " + name)),
            SoundSource::getName
    );

    private static final Codec<SoundSpec> DETAILED_CODEC = RecordCodecBuilder.create(ins -> ins.group(
            SoundEvent.CODEC.fieldOf("sound").forGetter(SoundSpec::sound),
            SOUND_SOURCE_CODEC.optionalFieldOf("source", SoundSource.MASTER).forGetter(SoundSpec::source),
            Codec.FLOAT.optionalFieldOf("volume", 1.0f).forGetter(SoundSpec::volume),
            Codec.FLOAT.optionalFieldOf("pitch", 1.0f).forGetter(SoundSpec::pitch),
            Codec.FLOAT.optionalFieldOf("min_volume", 0.0f).forGetter(SoundSpec::minVolume)
    ).apply(ins, SoundSpec::new));

    private static final Codec<SoundSpec> SIMPLE_CODEC = SoundEvent.CODEC.xmap(SoundSpec::of, SoundSpec::sound);

    public static final Codec<SoundSpec> CODEC = Codec.withAlternative(DETAILED_CODEC, SIMPLE_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, SoundSpec> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT), SoundSpec::sound,
            ByteBufCodecs.idMapper(i -> SoundSource.values()[i], SoundSource::ordinal), SoundSpec::source,
            ByteBufCodecs.FLOAT, SoundSpec::volume,
            ByteBufCodecs.FLOAT, SoundSpec::pitch,
            ByteBufCodecs.FLOAT, SoundSpec::minVolume,
            SoundSpec::new
    );

    private static Optional<SoundSource> bySourceName(String name) {
        for (SoundSource source : SoundSource.values()) {
            if (source.getName().equals(name)) return Optional.of(source);
        }
        return Optional.empty();
    }
}
