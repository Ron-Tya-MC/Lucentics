package io.github.rontyamc.lucentics.blocks.emitter.advanced_emitter;

import io.github.rontyamc.lucentics.blocks.emitter.EmitterBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class AdvancedEmitterBlockEntity extends EmitterBlockEntity {
    AdvancedEmitterBehavior behavior;

    public AdvancedEmitterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void attachBehavior(List<BlockEntityBehavior> behaviors) {
        behaviors.add(behavior = new AdvancedEmitterBehavior(this));
        super.attachBehavior(behaviors);
    }

    public AdvancedEmitterBehavior getEmitterBehavior() {
        return behavior;
    }

    public void tick() {
        behavior.tick();
    }

    public void dropContents(Level level, BlockPos pos) {
        behavior.dropContents(level, pos);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        CompoundTag behaviorTag = new CompoundTag();
        behavior.write(behaviorTag, provider, false);
        tag.put("emitter", behaviorTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        behavior.read(tag.getCompound("emitter"), provider, false);
    }
}
