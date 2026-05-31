package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBlockEntity;

public class LucenticsBlockEntityRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    public static final BlockEntityEntry<InjectorBlockEntity> INJECTOR = REGISTRATE.blockEntity("injector", InjectorBlockEntity::new)
            .validBlock(LucenticsBlockRegister.INJECTOR)
            .register();

    public static void register() {
    }
}
