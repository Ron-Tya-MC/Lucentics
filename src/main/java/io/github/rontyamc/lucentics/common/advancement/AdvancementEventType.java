package io.github.rontyamc.lucentics.common.advancement;

import com.mojang.serialization.Codec;
import io.github.rontyamc.lucentics.registers.LucenticsRegistries;
import net.minecraft.resources.ResourceLocation;

public class AdvancementEventType {
    public static final Codec<AdvancementEventType> CODEC = LucenticsRegistries.ADVANCEMENT_EVENT.byNameCodec();

    public ResourceLocation getId() {
        return LucenticsRegistries.ADVANCEMENT_EVENT.getKey(this);
    }
}
