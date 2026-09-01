package io.github.rontyamc.lucentics.blocks.tank;

import io.github.rontyamc.lucentics.common.FluidBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class TankRenderer extends FluidBlockRenderer {
    public TankRenderer(BlockEntityRendererProvider.Context context) {
        super(context, 2.0f / 16, 14.0f / 16, 2.0f / 16, 14.0f / 16, 2.0f / 16, 14.0f / 16);
    }
}
