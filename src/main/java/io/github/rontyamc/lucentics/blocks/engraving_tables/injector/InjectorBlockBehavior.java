package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import io.github.rontyamc.lucentics.common.GeneralBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import net.minecraft.world.Clearable;
import java.util.function.Supplier;

public class InjectorBlockBehavior extends BlockEntityBehavior implements Clearable {
    Supplier<Integer> maxStackSize;

    public InjectorBlockBehavior(GeneralBlockEntity be) {
        super(be);

        maxStackSize = () -> 1;
    }

    @Override
    public BehaviorType<?> getType() {
        return null;
    }

    @Override
    public void clearContent() {

    }

    @Override
    public void tick() {
        super.tick();


    }
}
