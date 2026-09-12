package io.github.rontyamc.lucentics.blocks.mixing_table;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.rontyamc.lucentics.common.util.TankRenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class MixingTableRenderer implements BlockEntityRenderer<MixingTableBlockEntity> {
    private static final TankRenderUtil.Bounds BOUNDS =
            new TankRenderUtil.Bounds(3.0f / 16, 13.0f / 16, 6.0f / 16, 11.0f / 16, 3.0f / 16, 13.0f / 16);

    public MixingTableRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(MixingTableBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        TankRenderUtil.render(be.getMixingTableBehavior().getContainerSlot(), BOUNDS, poseStack, bufferSource, packedLight);
    }
}
