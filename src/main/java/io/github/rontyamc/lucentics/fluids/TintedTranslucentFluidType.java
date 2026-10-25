package io.github.rontyamc.lucentics.fluids;

import com.tterrag.registrate.builders.FluidBuilder;
import io.github.rontyamc.lucentics.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class TintedTranslucentFluidType extends TintedFluidType {
    private final int tintColor;

    private Vector3f fogColor;
    private Supplier<Float> fogDistance;

    public static FluidBuilder.FluidTypeFactory create(int tintColor, int alpha, int fogColor, Supplier<Float> fogDistance) {
        return (p, s, f) -> {
            int alphaTintColor = (alpha << 24) | tintColor;
            TintedTranslucentFluidType fluidType = new TintedTranslucentFluidType(p, s, f, alphaTintColor);
            fluidType.fogColor = toVector3f(fogColor);
            fluidType.fogDistance = fogDistance;
            return fluidType;
        };
    }

    public static FluidBuilder.FluidTypeFactory create(int tintColor, int fogColor, Supplier<Float> fogDistance) {
        return (p, s, f) -> {
            TintedTranslucentFluidType fluidType = new TintedTranslucentFluidType(p, s, f, 0xFF000000 | tintColor);
            fluidType.fogColor = toVector3f(fogColor);
            fluidType.fogDistance = fogDistance;
            return fluidType;
        };
    }

    private TintedTranslucentFluidType(Properties properties, ResourceLocation stillTexture,
                                            ResourceLocation flowingTexture, int tintColor) {
        super(properties, stillTexture, flowingTexture);
        this.tintColor = tintColor;
    }

    @Override
    protected int getTintColor(FluidStack stack) {
        return this.tintColor;
    }

    @Override
    public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
        return this.tintColor;
    }

    @Override
    protected Vector3f getCustomFogColor() {
        return fogColor;
    }

    @Override
    protected float getFogDistanceModifier() {
        return fogDistance.get();
    }

    protected static Vector3f toVector3f(int color) {
        Float[] rgb = MiscUtil.separateRGB(color);
        return new Vector3f(rgb[0], rgb[1], rgb[2]);
    }
}
