package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.BlockEntry;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlock;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableBlock;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBlock;
import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBlock;
import io.github.rontyamc.lucentics.blocks.prism.prism_ritual.PrismRitualBlock;
import io.github.rontyamc.lucentics.registers.LucenticsTabRegister.CategoryType;

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
            .item()
            .build()
            .register();
    public static final BlockEntry<EmitterBlock> EMITTER = REGISTRATE.lucenticsBlockBuilder(CategoryType.MACHINES,"emitter", EmitterBlock::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();
    public static final BlockEntry<EngravingTableBlock> ENGRAVING_TABLE = REGISTRATE.lucenticsBlockBuilder(CategoryType.MACHINES,"engraving_table", EngravingTableBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();

    public static final BlockEntry<PedestalRitualBlock> PEDESTAL_RITUAL = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS,"pedestal_ritual", PedestalRitualBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();

    public static final BlockEntry<PrismRitualBlock> PRISM_RITUAL = REGISTRATE.lucenticsBlockBuilder(CategoryType.PRISMS, "prism_ritual", PrismRitualBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();

    public static void register() {
    }
}