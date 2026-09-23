package io.github.rontyamc.lucentics.blocks.assembling_table;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class AssemblingTableRenderer implements BlockEntityRenderer<AssemblingTableBlockEntity> {
    public AssemblingTableRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(AssemblingTableBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack container = blockEntity.getAssemblingTableBehavior().getContainer().asItemOrEmpty();

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.65f, 0.5f);
        poseStack.scale(0.3f, 0.3f, 0.3f);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getNextRotation()));

        itemRenderer.renderStatic(container, ItemDisplayContext.FIXED, LightTexture.pack(15,15), OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 1);
        poseStack.popPose();
    }
}
