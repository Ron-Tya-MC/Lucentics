package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.BlockEntry;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBlock;
import io.github.rontyamc.lucentics.registers.LucenticsTabRegister.CategoryType;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LucenticsBlockRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    static {
        REGISTRATE.setCreativeTab(LucenticsTabRegister.CREATIVE_MODE_TAB_BLOCKS);
    }

    public static final BlockEntry<Block> DUSK_BRICKS = REGISTRATE.lucenticsBlockBuilder(CategoryType.BLOCKS ,"dusk_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();

    public static final BlockEntry<InjectorBlock> INJECTOR = REGISTRATE.lucenticsBlockBuilder(CategoryType.MACHINES ,"injector", InjectorBlock::new)
            .initialProperties(() -> Blocks.BRICKS)
            .properties(p -> p.noOcclusion())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();

    public static void register() {
    }
}