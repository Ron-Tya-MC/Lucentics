package io.github.rontyamc.lucentics.blocks.milling_table;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class MillingTableRenderer implements BlockEntityRenderer<MillingTableBlockEntity> {
    public MillingTableRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(MillingTableBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float rotation_table = blockEntity.getNextRotationTable();
        float rotation_item = blockEntity.getNextRotationItem();
        float radius = 0.2f;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack container = blockEntity.getMillingTableBehavior().getContainer().asItemOrEmpty();
        int count = container.getCount();
        if (count == 0) return;
        if (count >= 12) count = 12;

        for (int i = 0; i < count; i++) {
            ItemStack stack = container.copyWithCount(1);

            float angle = 360f * i / count + rotation_table;
            if (angle >= 360) angle -= 360.0f;

            poseStack.pushPose();
            poseStack.translate(0.5f, 0.4f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            poseStack.translate(radius, 0, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-angle + rotation_item));
            poseStack.scale(0.2f, 0.2f, 0.2f);

            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, LightTexture.pack(15,15), OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), i);
            poseStack.popPose();
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
