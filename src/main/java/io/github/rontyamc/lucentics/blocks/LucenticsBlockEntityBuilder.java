package io.github.rontyamc.lucentics.blocks;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LucenticsBlockEntityBuilder<T extends BlockEntity, P> extends BlockEntityBuilder<T, P> {


    public static <T extends BlockEntity, P> BlockEntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
        return new LucenticsBlockEntityBuilder<>(owner, parent, name, callback, factory);
    }

    public LucenticsBlockEntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback,
                                       BlockEntityFactory<T> factory) {
        super(owner, parent, name, callback, factory);
    }



}
