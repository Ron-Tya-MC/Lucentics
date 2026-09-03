package io.github.rontyamc.lucentics.network;

import io.github.rontyamc.lucentics.blocks.milling_table.MillingTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class LucenticsNetwork {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                MillingProcessPayload.TYPE,
                MillingProcessPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    var level = Minecraft.getInstance().level;
                    if (level == null) return;
                    if (level.getBlockEntity(payload.pos()) instanceof MillingTableBlockEntity be) {
                        be.getMillingTableBehavior().applyClientProgress(payload.processingContinue(), payload.processingTimeMax());
                    }
                })
        );
    }
}
