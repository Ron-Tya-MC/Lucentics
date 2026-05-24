package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.BlockEntry;
import io.github.rontyamc.lucentics.Lucentics;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LucenticsBlockRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    public static final BlockEntry<Block> DUSK_BRICKS = REGISTRATE.block("blocks" ,"dusk_bricks", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();

    public static final BlockEntry<Block> DUSK_BRICKS_2 = REGISTRATE.block("blocks" ,"dusk_bricks_2", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();
    public static final BlockEntry<Block> DUSK_BRICKS_3 = REGISTRATE.block("ingredients" ,"dusk_bricks_3", Block::new)
            .initialProperties(() -> Blocks.BRICKS)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .build()
            .register();

    public static void register() {
        REGISTRATE.setCreativeTab(LucenticsTabRegister.CREATIVE_MODE_TAB_BLOCKS);
    }
}