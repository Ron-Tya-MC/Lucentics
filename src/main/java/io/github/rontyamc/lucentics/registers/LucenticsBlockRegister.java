package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.BlockEntry;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.emitter.EmitterBehavior;
import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlock;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableBehavior;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableBlock;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBehavior;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBlock;
import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBehavior;
import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_ritual.PrismRitualBlock;
import io.github.rontyamc.lucentics.common.behavior.BehaviorTypeBlockRegistry;
import io.github.rontyamc.lucentics.registers.LucenticsTabRegister.CategoryType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LucenticsBlockRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    static {
        REGISTRATE.setCreativeTab(LucenticsTabRegister.CREATIVE_MODE_TAB_BLOCKS);
    }

    public static final BlockEntry<Block> DUSK_BRICKS = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"dusk_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> RED_DUSK_BRICKS = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"red_dusk_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> BLUE_DUSK_BRICKS = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"blue_dusk_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> GREEN_DUSK_BRICKS = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"green_dusk_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> DAWNSTONE = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"dawnstone", Block::new)
            .initialProperties(() -> Blocks.STONE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();

    public static final BlockEntry<InjectorBlock> INJECTOR = REGISTRATE.lucenticsBlockBuilder(CategoryType.MACHINES,"injector", InjectorBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate((context, provider) -> {})
            .item()
            .build()
            .register();
    public static final BlockEntry<EmitterBlock> EMITTER = REGISTRATE.lucenticsBlockBuilder(CategoryType.MACHINES,"emitter", EmitterBlock::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate((context, provider) -> {})
            .item()
            .build()
            .register();
    public static final BlockEntry<EngravingTableBlock> ENGRAVING_TABLE = REGISTRATE.lucenticsBlockBuilder(CategoryType.MACHINES,"engraving_table", EngravingTableBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate((context, provider) -> {})
            .item()
            .build()
            .register();

    public static final BlockEntry<PedestalRitualBlock> PEDESTAL_RITUAL = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"pedestal_ritual", PedestalRitualBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate((context, provider) -> {})
            .item()
            .model((context, provider) -> provider.withExistingParent(context.getName(),
                    Lucentics.defaultLocation("block/pedestals/" + context.getName())))
            .build()
            .register();

    public static final BlockEntry<PrismRitualBlock> PRISM_RITUAL = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_ritual", PrismRitualBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate((context, provider) -> provider.simpleBlock(context.getEntry(),
                    provider.models().cubeAll(context.getName(),
                            Lucentics.defaultLocation("block/prisms/" + context.getName()))))
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
    }
}