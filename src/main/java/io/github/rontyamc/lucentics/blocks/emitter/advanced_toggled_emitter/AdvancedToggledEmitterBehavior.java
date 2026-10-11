package io.github.rontyamc.lucentics.blocks.emitter.advanced_toggled_emitter;

import io.github.rontyamc.lucentics.blocks.emitter.advanced_emitter.AdvancedEmitterBehavior;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class AdvancedToggledEmitterBehavior extends AdvancedEmitterBehavior {
    public static final BehaviorType<AdvancedToggledEmitterBehavior> TYPE = new BehaviorType<>("advanced_toggled_emitter", AdvancedEmitterBehavior.TYPE);

    protected boolean wasPowered = false;

    public AdvancedToggledEmitterBehavior(BaseBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void whilePowered(Level level, BlockPos pos) {
        if (!wasPowered) {
            setStopBeam(!stopBeam);
        }
        wasPowered = true;
    }

    @Override
    public void whileUnPowered(Level level, BlockPos pos) {
        wasPowered = false;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putBoolean("was_powered", wasPowered);
        super.write(nbt, registries, clientPacket);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        wasPowered = nbt.getBoolean("was_powered");
        super.read(nbt, registries, clientPacket);
    }
}
