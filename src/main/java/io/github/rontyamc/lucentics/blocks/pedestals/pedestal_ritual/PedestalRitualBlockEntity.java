package io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual;

import io.github.rontyamc.lucentics.blocks.pedestals.PedestalBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PedestalRitualBlockEntity extends PedestalBlockEntity {
    PedestalRitualBehavior behavior;

    public PedestalRitualBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void attachBehavior(List<BlockEntityBehavior> behaviors) {
        behaviors.add(behavior = new PedestalRitualBehavior(this));
    }

    public PedestalRitualBehavior getPedestalRitualBehavior() {return behavior;}

    @Override
    public PedestalRitualBehavior getBehavior() {return behavior;}

    public void dropContents(Level level, BlockPos pos) {
        behavior.dropContents(level, pos);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag behaviorTag = new CompoundTag();
        behavior.write(behaviorTag, provider, false);
        tag.put("pedestal", behaviorTag);
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        behavior.read(tag.getCompound("pedestal"), provider, false);
    }
}
