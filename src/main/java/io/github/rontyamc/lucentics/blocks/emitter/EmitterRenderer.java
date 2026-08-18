package io.github.rontyamc.lucentics.blocks.emitter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class EmitterRenderer implements BlockEntityRenderer<EmitterBlockEntity> {
    private static final ResourceLocation BEAM_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");

    public EmitterRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(EmitterBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        renderLens(blockEntity, poseStack, bufferSource);
        renderBeam(blockEntity, partialTick, poseStack, bufferSource);
    }

    public void renderLens(EmitterBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack lens = blockEntity.inventory.getStackInSlot(0);

        poseStack.pushPose();
        poseStack.translate(0.0f, 1.0f, 0.0f);
        poseStack.scale(1.0f, 1.0f, 1.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        itemRenderer.renderStatic(lens, ItemDisplayContext.FIXED, getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 1);
        poseStack.popPose();
    }

    private void renderBeam(EmitterBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        EmitterBehavior behavior = blockEntity.getEmitterBehavior();
        int length = behavior.getBeamLength();
        if (length <= 0) return;

        Direction facing = blockEntity.getBlockState().getValue(EmitterBlock.FACING);
        int argb = toArgb(behavior.getColor());
        long gameTime = blockEntity.getLevel().getGameTime();

        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0);

        switch (facing) {
            case NORTH -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                poseStack.translate(0, 0, -0.5);
            }
            case SOUTH -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.translate(0, 0, -0.5);
            }
            case WEST -> {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                poseStack.translate(-0.5, 0, 0);
            }
            case EAST -> {
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                poseStack.translate(-0.5, 0, 0);
            }
            default -> {}
        }

        BeaconRenderer.renderBeaconBeam(poseStack, bufferSource, BEAM_TEXTURE,
                partialTick, 1.0f, gameTime, 0, length+1, argb, 0.15f, 0.2f);

        poseStack.popPose();
    }

    private static int toArgb(Colors color) {
        return 0xFF000000 | (color.getColorCode() & 0x00FFFFFF);
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(EmitterBlockEntity blockEntity) {
        return net.minecraft.world.phys.AABB.INFINITE;
    }
}
