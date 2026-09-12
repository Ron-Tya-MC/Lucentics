package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.FluidEntry;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.util.ModelUtil;
import io.github.rontyamc.lucentics.fluids.TintedTranslucentFluidType;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister.LucenticsFTags;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.EnumMap;
import java.util.Map;

public class LucenticsFluidRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    public static final Map<Colors, FluidEntry<BaseFlowingFluid.Flowing>> DYE_LIQUIDS = registerDyeLiquids();

    private static Map<Colors, FluidEntry<BaseFlowingFluid.Flowing>> registerDyeLiquids() {
        Map<Colors, FluidEntry<BaseFlowingFluid.Flowing>> map = new EnumMap<>(Colors.class);
        for (Colors color : Colors.values()) {
            if (color == Colors.SUNLIGHT) continue;
            String name = color.getName() + "_dye_liquid";
            FluidEntry<BaseFlowingFluid.Flowing> entry = REGISTRATE.fluidItemRegister(LucenticsTabRegister.CategoryType.BLOCKS, name)
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
            FluidEntry<BaseFlowingFluid.Flowing> entry = REGISTRATE.fluidItemRegister(LucenticsTabRegister.CategoryType.BLOCKS, name)
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

    public static void register() {
    }
}
