package io.github.rontyamc.lucentics.common.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.rontyamc.lucentics.common.FluidSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public final class TankRenderUtil {
    private TankRenderUtil() {}

    public record Bounds(float x0, float x1, float y0, float y1, float z0, float z1) {}

    private static void vertex(VertexConsumer consumer, Matrix4f pose,
                               float x, float y, float z,
                               float r, float g, float b, float a,
                               float u, float v, int light,
                               float nx, float ny, float nz) {
        consumer.addVertex(pose, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setLight(light)
                .setNormal(nx, ny, nz);
    }

    private static void quad(VertexConsumer consumer, Matrix4f pose,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3,
                             float u0, float v0, float u1, float v1,
                             float nx, float ny, float nz,
                             float r, float g, float b, float a, int light) {
        vertex(consumer, pose, x0, y0, z0, r, g, b, a, u0, v1, light, nx, ny, nz);
        vertex(consumer, pose, x1, y1, z1, r, g, b, a, u0, v0, light, nx, ny, nz);
        vertex(consumer, pose, x2, y2, z2, r, g, b, a, u1, v0, light, nx, ny, nz);
        vertex(consumer, pose, x3, y3, z3, r, g, b, a, u1, v1, light, nx, ny, nz);
    }

    public static void render(FluidSlot slot, Bounds bounds,
                              PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        FluidStack fluidStack = slot.getContent();
        int capacity = slot.getCapacity();
        if (fluidStack.isEmpty() || capacity <= 0) return;

        float ratio = Mth.clamp((float) fluidStack.getAmount() / capacity, 0f, 1f);
        float y1 = bounds.y0 + (bounds.y1 - bounds.y0) * ratio;
        if (y1 <= bounds.y0) return;

        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid().getFluidType());

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(extensions.getStillTexture(fluidStack));

        int tint = extensions.getTintColor(fluidStack);
        float r = ((tint >> 16) & 0xFF) / 255f;
        float g = ((tint >> 8) & 0xFF) / 255f;
        float b = (tint & 0xFF) / 255f;
        float a = 1.0f;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f pose = poseStack.last().pose();

        float hu0 = sprite.getU(bounds.x0), hu1 = sprite.getU(bounds.x1);
        float hv0 = sprite.getV(bounds.z0), hv1 = sprite.getV(bounds.z1);
        float vu0 = sprite.getU0(), vu1 = sprite.getU1();
        float vv0 = sprite.getV0(), vv1 = vv0 + (sprite.getV1() - vv0) * ratio;

        // 上面
        quad(consumer, pose, bounds.x0, y1, bounds.z0, bounds.x0, y1, bounds.z1, bounds.x1, y1, bounds.z1, bounds.x1, y1, bounds.z0,
                hu0, hv0, hu1, hv1, 0, 1, 0, r, g, b, a, packedLight);
        // 北面
        quad(consumer, pose, bounds.x0, bounds.y0, bounds.z0, bounds.x0, y1, bounds.z0, bounds.x1, y1, bounds.z0, bounds.x1, bounds.y0, bounds.z0,
                vu0, vv0, vu1, vv1, 0, 0, -1, r, g, b, a, packedLight);
        // 南面
        quad(consumer, pose, bounds.x1, bounds.y0, bounds.z1, bounds.x1, y1, bounds.z1, bounds.x0, y1, bounds.z1, bounds.x0, bounds.y0, bounds.z1,
                vu0, vv0, vu1, vv1, 0, 0, 1, r, g, b, a, packedLight);
        // 西面
        quad(consumer, pose, bounds.x0, bounds.y0, bounds.z1, bounds.x0, y1, bounds.z1, bounds.x0, y1, bounds.z0, bounds.x0, bounds.y0, bounds.z0,
                vu0, vv0, vu1, vv1, -1, 0, 0, r, g, b, a, packedLight);
        // 東面
        quad(consumer, pose, bounds.x1, bounds.y0, bounds.z0, bounds.x1, y1, bounds.z0, bounds.x1, y1, bounds.z1, bounds.x1, bounds.y0, bounds.z1,
                vu0, vv0, vu1, vv1, 1, 0, 0, r, g, b, a, packedLight);
    }
}
