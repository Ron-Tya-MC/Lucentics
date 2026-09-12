package io.github.rontyamc.lucentics.blocks.tank;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.rontyamc.lucentics.common.util.TankRenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class TankRenderer implements BlockEntityRenderer<TankBlockEntity> {
    private static final TankRenderUtil.Bounds BOUNDS =
            new TankRenderUtil.Bounds(2.0f / 16, 14.0f / 16, 2.0f / 16, 14.0f / 16, 2.0f / 16, 14.0f / 16);

    public TankRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TankBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        TankRenderUtil.render(be.getBehavior().getTank(), BOUNDS, poseStack, bufferSource, packedLight);
    }
}
