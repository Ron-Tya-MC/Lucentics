package io.github.rontyamc.lucentics.registers;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.recipes.crushing.CrushingProgressEntry;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class LucenticsAttachmentRegister {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Lucentics.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Map<BlockPos, CrushingProgressEntry>>> CRUSHING_PROGRESS =
            ATTACHMENT_TYPES.register("crushing_progress", () -> AttachmentType
                    .builder(() -> (Map<BlockPos, CrushingProgressEntry>) new HashMap<BlockPos, CrushingProgressEntry>())
                    .serialize(CrushingProgressEntry.MAP_CODEC)
                    .build());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
