package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.BlockEntry;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.assembling_table.AssemblingTableBehavior;
import io.github.rontyamc.lucentics.blocks.assembling_table.AssemblingTableBlock;
import io.github.rontyamc.lucentics.blocks.emitter.EmitterBehavior;
import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlock;
import io.github.rontyamc.lucentics.blocks.emitter.toggled_emitter.ToggledEmitterBlock;
import io.github.rontyamc.lucentics.blocks.engraving_table.EngravingTableBehavior;
import io.github.rontyamc.lucentics.blocks.engraving_table.EngravingTableBlock;
import io.github.rontyamc.lucentics.blocks.injector.InjectorBehavior;
import io.github.rontyamc.lucentics.blocks.injector.InjectorBlock;
import io.github.rontyamc.lucentics.blocks.milling_table.MillingTableBehavior;
import io.github.rontyamc.lucentics.blocks.milling_table.MillingTableBlock;
import io.github.rontyamc.lucentics.blocks.mixing_table.MixingTableBehavior;
import io.github.rontyamc.lucentics.blocks.mixing_table.MixingTableBlock;
import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBehavior;
import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBlock;
import io.github.rontyamc.lucentics.blocks.prism.PrismBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_attack.PrismAttackBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_break.PrismBreakBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_collecting.PrismCollectingBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_dyeing.PrismDyeingBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_exporting.PrismExportingBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_fluid_place.PrismFluidPlaceBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_importing.PrismImportingBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_io.PrismIOBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_place.PrismPlaceBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_ritual.PrismRitualBlock;
import io.github.rontyamc.lucentics.blocks.tank.TankBehavior;
import io.github.rontyamc.lucentics.blocks.tank.light_copper_tank.TankLightCopperBlock;
import io.github.rontyamc.lucentics.common.behavior.BehaviorTypeBlockRegistry;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.util.MiscUtil;
import io.github.rontyamc.lucentics.common.util.ModelUtil;
import io.github.rontyamc.lucentics.registers.LucenticsRenderTypeRegister.Layer;
import io.github.rontyamc.lucentics.registers.LucenticsTabRegister.CategoryType;
import io.github.rontyamc.lucentics.registers.LucenticsTagRegister.LucenticsBTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("Convert2MethodRef")
public class LucenticsBlockRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    static {
        REGISTRATE.setCreativeTab(LucenticsTabRegister.CREATIVE_MODE_TAB_BLOCKS);
    }

    public static final BlockEntry<Block> DUSK_BRICKS = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"dusk_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.DUSK_BRICKS.tag)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> RED_DUSK_BRICKS = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"red_dusk_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.DUSK_BRICKS.tag)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> BLUE_DUSK_BRICKS = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"blue_dusk_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.DUSK_BRICKS.tag)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> GREEN_DUSK_BRICKS = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"green_dusk_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.DUSK_BRICKS.tag)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> DAWNSTONE = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"dawnstone", Block::new)
            .initialProperties(() -> Blocks.STONE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> LIGHT_COPPER_BLOCK = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"light_copper_block", Block::new)
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.LIGHT_COPPER_BLOCKS.tag)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> YELLOW_LIGHT_COPPER_BLOCK = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"yellow_light_copper_block", Block::new)
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.LIGHT_COPPER_BLOCKS.tag)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> MAGENTA_LIGHT_COPPER_BLOCK = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"magenta_light_copper_block", Block::new)
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.LIGHT_COPPER_BLOCKS.tag)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> CYAN_LIGHT_COPPER_BLOCK = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"cyan_light_copper_block", Block::new)
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.LIGHT_COPPER_BLOCKS.tag)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> GLIMMER_IRON_BLOCK = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"glimmer_iron_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.GLIMMER_IRON_BLOCKS.tag)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> PINK_GLIMMER_IRON_BLOCK = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"pink_glimmer_iron_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.GLIMMER_IRON_BLOCKS.tag)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> LIME_GLIMMER_IRON_BLOCK = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"lime_glimmer_iron_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.GLIMMER_IRON_BLOCKS.tag)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> LIGHT_BLUE_GLIMMER_IRON_BLOCK = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"light_blue_glimmer_iron_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.GLIMMER_IRON_BLOCKS.tag)
            .item()
            .build()
            .register();

    public static final BlockEntry<InjectorBlock> INJECTOR = REGISTRATE.lucenticsBlockBuilder(CategoryType.FUNCTIONAL,"injector", InjectorBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(ModelUtil.simpleBlockState(""))
            .item()
            .build()
            .register();
    public static final BlockEntry<EmitterBlock> EMITTER = REGISTRATE.lucenticsBlockBuilder(CategoryType.FUNCTIONAL,"emitter", EmitterBlock::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(ModelUtil.horizontalDirectionalBlockState(""))
            .item()
            .build()
            .register();
    public static final BlockEntry<ToggledEmitterBlock> TOGGLED_EMITTER = REGISTRATE.lucenticsBlockBuilder(CategoryType.FUNCTIONAL,"toggled_emitter", ToggledEmitterBlock::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(ModelUtil.horizontalDirectionalBlockState(""))
            .item()
            .build()
            .register();
    public static final BlockEntry<EngravingTableBlock> ENGRAVING_TABLE = REGISTRATE.lucenticsBlockBuilder(CategoryType.FUNCTIONAL,"engraving_table", EngravingTableBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(ModelUtil.simpleBlockState(""))
            .item()
            .build()
            .register();
    public static final BlockEntry<MillingTableBlock> MILLING_TABLE = REGISTRATE.lucenticsBlockBuilder(CategoryType.FUNCTIONAL,"milling_table", MillingTableBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(ModelUtil.simpleBlockState(""))
            .item()
            .build()
            .register();
    public static final BlockEntry<MixingTableBlock> MIXING_TABLE = REGISTRATE.lucenticsBlockBuilder(CategoryType.FUNCTIONAL,"mixing_table", MixingTableBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(ModelUtil.simpleBlockState(""))
            .item()
            .build()
            .register();
    public static final BlockEntry<AssemblingTableBlock> ASSEMBLING_TABLE = REGISTRATE.lucenticsBlockBuilder(CategoryType.FUNCTIONAL,"assembling_table", AssemblingTableBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(ModelUtil.simpleBlockState(""))
            .item()
            .build()
            .register();

    public static final BlockEntry<PedestalRitualBlock> PEDESTAL_RITUAL = REGISTRATE.lucenticsBlockBuilder(CategoryType.FUNCTIONAL,"pedestal_ritual", PedestalRitualBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PEDESTALS.tag, LucenticsBTags.PEDESTAL_RITUAL.tag)
            .blockstate(ModelUtil.simpleBlockState("pedestals"))
            .item()
            .model((context, provider) -> provider.withExistingParent(context.getName(),
                    Lucentics.defaultLocation("block/pedestals/" + context.getName())))
            .build()
            .register();

    public static final BlockEntry<TankLightCopperBlock> TANK_LIGHT_COPPER = REGISTRATE.lucenticsBlockBuilder(CategoryType.FUNCTIONAL,"tank_light_copper", TankLightCopperBlock::new)
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(ModelUtil.simpleBlockState("tanks"))
            .lang("Light Copper Tank")
            .item()
            .model((context, provider) -> provider.withExistingParent(context.getName(),
                    Lucentics.defaultLocation("block/tanks/" + context.getName())))
            .build()
            .register();
    public static final BlockEntry<TankLightCopperBlock> TANK_LIGHT_COPPER_BOLD = REGISTRATE.lucenticsBlockBuilder(CategoryType.FUNCTIONAL,"tank_light_copper_bold", TankLightCopperBlock::new)
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(ModelUtil.simpleBlockState("tanks"))
            .lang("Bold Light Copper Tank")
            .item()
            .model((context, provider) -> provider.withExistingParent(context.getName(),
                    Lucentics.defaultLocation("block/tanks/" + context.getName())))
            .build()
            .register();

    public static final BlockEntry<PrismBlock> PRISM_BLANK = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_blank", PrismBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
            .blockstate(ModelUtil.cubeAllBlockState("prisms"))
            .lang("Blank Prism")
            .item()
            .build()
            .register();

    public static final Map<Colors, BlockEntry<PrismBlock>> COLORED_PRISM_BLANK = registerPrismBlanks();

    private static Map<Colors, BlockEntry<PrismBlock>> registerPrismBlanks() {
        Map<Colors, BlockEntry<PrismBlock>> map = new EnumMap<>(Colors.class);
        for (Colors color : Colors.values()) {
            if (color == Colors.SUNLIGHT) continue;
            String name = "prism_blank_" + color.getName();
            BlockEntry<PrismBlock> entry = REGISTRATE
                    .lucenticsBlockBuilder(CategoryType.PRISMS, name, PrismBlock::new)
                    .initialProperties(() -> Blocks.GLASS)
                    .properties(p -> p.noOcclusion())
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
                    .blockstate(ModelUtil.cubeAllBlockState("prisms"))
                    .lang(MiscUtil.toPascalCase(color.getName()) + " Blank Prism")
                    .item()
                    .build()
                    .register();
            map.put(color, entry);
        }
        return map;
    }

    public static final BlockEntry<PrismRitualBlock> PRISM_RITUAL = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_ritual", PrismRitualBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag, LucenticsBTags.PRISM_RITUAL.tag)
            .blockstate(ModelUtil.cubeColumnBlockState("prisms/prism_ritual", "prisms/prism_blank"))
            .lang("Ritual Prism")
            .item()
            .build()
            .register();
    public static final BlockEntry<PrismImportingBlock> PRISM_IMPORTING = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_importing", PrismImportingBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
            .blockstate(ModelUtil.cubeColumnBlockState("prisms/prism_importing", "prisms/prism_blank"))
            .lang("Importing Prism")
            .item()
            .build()
            .register();
    public static final BlockEntry<PrismExportingBlock> PRISM_EXPORTING = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_exporting", PrismExportingBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
            .blockstate(ModelUtil.cubeColumnBlockState("prisms/prism_exporting", "prisms/prism_blank"))
            .lang("Exporting Prism")
            .item()
            .build()
            .register();
    public static final BlockEntry<PrismIOBlock> PRISM_IO = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_io", PrismIOBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
            .blockstate(ModelUtil.cubeColumnBlockState("prisms/prism_io", "prisms/prism_blank"))
            .lang("IO Prism")
            .item()
            .build()
            .register();
    public static final BlockEntry<PrismCollectingBlock> PRISM_COLLECTING = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_collecting", PrismCollectingBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
            .blockstate(ModelUtil.cubeColumnBlockState("prisms/prism_collecting", "prisms/prism_blank"))
            .lang("Collecting Prism")
            .item()
            .build()
            .register();
    public static final BlockEntry<PrismBreakBlock> PRISM_BREAK = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_break", PrismBreakBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
            .blockstate(ModelUtil.cubeColumnBlockState("prisms/prism_break", "prisms/prism_blank_red"))
            .lang("Destruction Prism")
            .item()
            .build()
            .register();
    public static final BlockEntry<PrismAttackBlock> PRISM_ATTACK = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_attack", PrismAttackBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
            .blockstate(ModelUtil.cubeColumnBlockState("prisms/prism_attack", "prisms/prism_blank_red"))
            .lang("Attack Prism")
            .item()
            .build()
            .register();
    public static final BlockEntry<PrismFluidPlaceBlock> PRISM_FLUID_PLACE = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_fluid_place", PrismFluidPlaceBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
            .blockstate(ModelUtil.cubeColumnBlockState("prisms/prism_fluid_place", "prisms/prism_blank_blue"))
            .lang("Fluid Placing Prism")
            .item()
            .build()
            .register();
    public static final BlockEntry<PrismFluidDrainBlock> PRISM_FLUID_DRAIN = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_fluid_drain", PrismFluidDrainBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
            .blockstate(ModelUtil.cubeColumnBlockState("prisms/prism_fluid_drain", "prisms/prism_blank_blue"))
            .lang("Draining Prism")
            .item()
            .build()
            .register();
    public static final BlockEntry<PrismPlaceBlock> PRISM_PLACE = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_place", PrismPlaceBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
            .blockstate(ModelUtil.cubeColumnBlockState("prisms/prism_place", "prisms/prism_blank_green"))
            .lang("Construction Prism")
            .item()
            .build()
            .register();
    public static final BlockEntry<PrismDyeingBlock> PRISM_DYEING = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_dyeing", PrismDyeingBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, LucenticsBTags.PRISMS.tag)
            .blockstate(ModelUtil.cubeColumnBlockState("prisms/prism_dyeing", "prisms/prism_blank_green"))
            .lang("Dyeing Prism")
            .item()
            .build()
            .register();

    public static void register() {
    }

    static {
        BehaviorTypeBlockRegistry.register(INJECTOR::asStack, InjectorBehavior.TYPE);
        BehaviorTypeBlockRegistry.register(EMITTER::asStack,EmitterBehavior.TYPE);
        BehaviorTypeBlockRegistry.register(ENGRAVING_TABLE::asStack, EngravingTableBehavior.TYPE);
        BehaviorTypeBlockRegistry.register(PEDESTAL_RITUAL::asStack, PedestalRitualBehavior.TYPE);
        BehaviorTypeBlockRegistry.register(TANK_LIGHT_COPPER::asStack, TankBehavior.TYPE);
        BehaviorTypeBlockRegistry.register(TANK_LIGHT_COPPER_BOLD::asStack, TankBehavior.TYPE);
        BehaviorTypeBlockRegistry.register(MILLING_TABLE::asStack, MillingTableBehavior.TYPE);
        BehaviorTypeBlockRegistry.register(MIXING_TABLE::asStack, MixingTableBehavior.TYPE);
        BehaviorTypeBlockRegistry.register(ASSEMBLING_TABLE::asStack, AssemblingTableBehavior.TYPE);

        LucenticsRenderTypeRegister.registerBlock(PRISM_BLANK, Layer.TRANSLUCENT);
        for (Colors color : Colors.values()) {
            if (color.equals(Colors.SUNLIGHT)) continue;
            LucenticsRenderTypeRegister.registerBlock(COLORED_PRISM_BLANK.get(color), Layer.TRANSLUCENT);
        }
        LucenticsRenderTypeRegister.registerBlock(PRISM_RITUAL, Layer.TRANSLUCENT);
        LucenticsRenderTypeRegister.registerBlock(PRISM_IMPORTING, Layer.TRANSLUCENT);
        LucenticsRenderTypeRegister.registerBlock(PRISM_EXPORTING, Layer.TRANSLUCENT);
        LucenticsRenderTypeRegister.registerBlock(PRISM_IO, Layer.TRANSLUCENT);
        LucenticsRenderTypeRegister.registerBlock(PRISM_COLLECTING, Layer.TRANSLUCENT);
        LucenticsRenderTypeRegister.registerBlock(PRISM_BREAK, Layer.TRANSLUCENT);
        LucenticsRenderTypeRegister.registerBlock(PRISM_ATTACK, Layer.TRANSLUCENT);
        LucenticsRenderTypeRegister.registerBlock(PRISM_DYEING, Layer.TRANSLUCENT);
        LucenticsRenderTypeRegister.registerBlock(PRISM_PLACE, Layer.TRANSLUCENT);
    }
}