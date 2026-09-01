package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlockEntity;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableBlock;
import io.github.rontyamc.lucentics.blocks.engraving_tables.engraving_table.EngravingTableBlockEntity;
import io.github.rontyamc.lucentics.blocks.engraving_tables.injector.InjectorBlockEntity;
import io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual.PedestalRitualBlockEntity;
import io.github.rontyamc.lucentics.blocks.tank.light_copper_tank.TankLightCopperBlockEntity;

public class LucenticsBlockEntityRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    public static final BlockEntityEntry<InjectorBlockEntity> INJECTOR = REGISTRATE.blockEntity("injector", InjectorBlockEntity::new)
            .validBlock(LucenticsBlockRegister.INJECTOR)
            .register();
    public static final BlockEntityEntry<EmitterBlockEntity> EMITTER = REGISTRATE.blockEntity("emitter", EmitterBlockEntity::new)
            .validBlock(LucenticsBlockRegister.EMITTER)
            .register();
    public static final BlockEntityEntry<EngravingTableBlockEntity> ENGRAVING_TABLE = REGISTRATE.blockEntity("engraving_table", EngravingTableBlockEntity::new)
            .validBlock(LucenticsBlockRegister.ENGRAVING_TABLE)
            .register();

    public static final BlockEntityEntry<PedestalRitualBlockEntity> PEDESTAL_RITUAL = REGISTRATE.blockEntity("pedestal_ritual", PedestalRitualBlockEntity::new)
            .validBlock(LucenticsBlockRegister.PEDESTAL_RITUAL)
            .register();

    public static final BlockEntityEntry<TankLightCopperBlockEntity> TANK_LIGHT_COPPER = REGISTRATE.blockEntity("tank_light_copper", TankLightCopperBlockEntity::new)
            .validBlock(LucenticsBlockRegister.TANK_LIGHT_COPPER)
            .validBlock(LucenticsBlockRegister.TANK_LIGHT_COPPER_BOLD)
            .register();

    public static void register() {
    }
}
