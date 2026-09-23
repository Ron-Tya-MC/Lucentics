package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.FluidEntry;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.util.ModelUtil;
import io.github.rontyamc.lucentics.fluids.TintedTranslucentFluidType;
import io.github.rontyamc.lucentics.registers.LucenticsTabRegister.CategoryType;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister.LucenticsFTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LucenticsFluidRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    public static Map<Fluid, Colors> coloredFluids = new HashMap<>();

    public static final Map<Colors, FluidEntry<BaseFlowingFluid.Flowing>> DYE_LIQUIDS = registerDyeLiquids();

    private static Map<Colors, FluidEntry<BaseFlowingFluid.Flowing>> registerDyeLiquids() {
        Map<Colors, FluidEntry<BaseFlowingFluid.Flowing>> map = new EnumMap<>(Colors.class);
        for (Colors color : Colors.values()) {
            if (color == Colors.SUNLIGHT) continue;
            String name = color.getName() + "_dye_liquid";
            FluidEntry<BaseFlowingFluid.Flowing> entry = REGISTRATE.fluidItemRegister(CategoryType.BLOCKS, name)
                    .waterLike(name, TintedTranslucentFluidType.create(color.getColorCode(), 255, color.getColorCode(), () -> 0.1f))
                    .properties(p -> p.density(1000).viscosity(1000))
                    .source(BaseFlowingFluid.Source::new)
                    .tag(LucenticsFTags.DYE_LIQUIDS.tag)
                    .block()
                    .build()
                    .bucket()
                    .model(ModelUtil.generatedItemModel("buckets"))
                    .build()
                    .register();
            map.put(color, entry);

            LucenticsRenderTypeRegister.registerFluid(entry, LucenticsRenderTypeRegister.Layer.TRANSLUCENT);
            LucenticsRenderTypeRegister.registerFluid(() -> entry.get().getSource(), LucenticsRenderTypeRegister.Layer.TRANSLUCENT);
        }
        return map;
    }

    public static final Map<Colors, FluidEntry<BaseFlowingFluid.Flowing>> COLOQUIDS = registerColoquids();

    private static Map<Colors, FluidEntry<BaseFlowingFluid.Flowing>> registerColoquids() {
        Map<Colors, FluidEntry<BaseFlowingFluid.Flowing>> map = new EnumMap<>(Colors.class);
        for (Colors color : Colors.values()) {
            if (color == Colors.SUNLIGHT) continue;
            String name = color.getName() + "_coloquid";
            FluidEntry<BaseFlowingFluid.Flowing> entry = REGISTRATE.fluidItemRegister(CategoryType.BLOCKS, name)
                    .fluid(name, Lucentics.defaultLocation("block/coloquid_still"),
                            Lucentics.defaultLocation("block/coloquid_flow"),
                            TintedTranslucentFluidType.create(color.getColorCode(), 255, color.getColorCode(), () -> 0.1f))
                    .properties(p -> p.density(1500).viscosity(1500))
                    .fluidProperties(p -> p.tickRate(15))
                    .source(BaseFlowingFluid.Source::new)
                    .tag(LucenticsFTags.COLOQUIDS.tag)
                    .block()
                    .build()
                    .bucket()
                    .model(ModelUtil.generatedItemModel("buckets"))
                    .build()
                    .register();
            map.put(color, entry);

            LucenticsRenderTypeRegister.registerFluid(entry, LucenticsRenderTypeRegister.Layer.TRANSLUCENT);
            LucenticsRenderTypeRegister.registerFluid(() -> entry.get().getSource(), LucenticsRenderTypeRegister.Layer.TRANSLUCENT);
        }
        return map;
    }

    public static final FluidEntry<BaseFlowingFluid.Flowing> CLAY_WATER = REGISTRATE.fluidItemRegister(CategoryType.BLOCKS, "clay_water")
            .fluid("clay_water", Lucentics.defaultLocation("block/clay_water_still"),
                    Lucentics.defaultLocation("block/clay_water_flow"))
            .properties(p -> p.density(1800).viscosity(1200))
            .fluidProperties(p -> p.tickRate(15))
            .source(BaseFlowingFluid.Source::new)
            .block()
            .build()
            .bucket()
            .model(ModelUtil.generatedItemModel("buckets"))
            .build()
            .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> PATINA_LIQUID = REGISTRATE.fluidItemRegister(CategoryType.BLOCKS, "patina_liquid")
            .fluid("patina_liquid", Lucentics.defaultLocation("block/patina_liquid_still"),
                    Lucentics.defaultLocation("block/patina_liquid_flow"))
            .properties(p -> p.density(1500).viscosity(1000))
            .source(BaseFlowingFluid.Source::new)
            .block()
            .build()
            .bucket()
            .model(ModelUtil.generatedItemModel("buckets"))
            .build()
            .register();

    public static void register() {
    }

    // 初期化
    private static Map<Fluid, Colors> fluidColorMap() {
        if (coloredFluids.isEmpty()) {
            Map<Fluid, Colors> map = new HashMap<>();
            DYE_LIQUIDS.forEach((color, entry) -> {
                BaseFlowingFluid.Flowing flowing = entry.get();
                map.put(flowing.getSource(), color);
                map.put(flowing, color);
            });
            COLOQUIDS.forEach((color, entry) -> {
                BaseFlowingFluid.Flowing flowing = entry.get();
                map.put(flowing.getSource(), color);
                map.put(flowing, color);
            });
            coloredFluids = map;
        }
        return coloredFluids;
    }

    /**
     * 予め{@link LucenticsFluidRegister#registerFluidColor(Fluid fluid, Colors color)}等を用いてHashMapに登録した液体の色を取得する。
     *
     * @param fluid 色を取得したい液体
     *
     * @return その液体の色(存在すれば)
     */
    public static Optional<Colors> getColor(Fluid fluid) {

        if (fluid == null || fluid == Fluids.EMPTY) return Optional.empty();
        return Optional.ofNullable(fluidColorMap().get(fluid));
    }

    /**
     * 色付き流体の、流体とその色をHashMapに登録する。
     * {@link LucenticsFluidRegister#getColor(Fluid fluid)}を使うことで登録した色を得られる。
     *
     * @param fluid 登録したい色付き流体。
     * @param color 登録したい流体の色。
     */
    public static void registerFluidColor(Fluid fluid, Colors color) {
        if (fluid == null || fluid == Fluids.EMPTY || color == null) return;
        fluidColorMap().put(fluid, color);
    }

    /**
     * {@link LucenticsFluidRegister#registerFluidColor(Fluid fluid, Colors color)}のDyeColorで登録するオーバーロード。
     *
     * @param fluid 登録したい色付き流体。
     * @param dyeColor 登録したい流体の色。
     */
    public static void registerFluidColor(Fluid fluid, DyeColor dyeColor) {
        registerFluidColor(fluid, Colors.byDyeColor(dyeColor));
    }
}
