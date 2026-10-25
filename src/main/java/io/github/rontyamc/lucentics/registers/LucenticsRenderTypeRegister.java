package io.github.rontyamc.lucentics.registers;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LucenticsRenderTypeRegister {
    public enum Layer {
        CUTOUT,
        CUTOUT_MIPPED,
        TRANSLUCENT
    }

    public record Entry<T>(Supplier<? extends T> value, Layer layer) {
        public RenderType toRenderType() {
            return switch (this.layer) {
                case CUTOUT -> RenderType.cutout();
                case CUTOUT_MIPPED -> RenderType.cutoutMipped();
                case TRANSLUCENT -> RenderType.translucent();
            };
        }
    }

    private static final List<Entry<Block>> BLOCKS = new ArrayList<>();
    private static final List<Entry<Fluid>> FLUIDS = new ArrayList<>();

    public static void registerBlock(Supplier<? extends Block> block, Layer layer) {
        BLOCKS.add(new Entry<>(block, layer));
    }

    public static void registerFluid(Supplier<? extends Fluid> fluid, Layer layer) {
        FLUIDS.add(new Entry<>(fluid, layer));
    }

    public static List<Entry<Block>> getBlocks() { return BLOCKS; }
    public static List<Entry<Fluid>> getFluids() { return FLUIDS; }
}
