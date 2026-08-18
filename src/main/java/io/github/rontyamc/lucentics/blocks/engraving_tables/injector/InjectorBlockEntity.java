package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InjectorBlockEntity extends BaseBlockEntity {
    InjectorBehavior behavior;
    private float rotation;

    public InjectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void attachBehavior(List<BlockEntityBehavior> behaviors) {
        behaviors.add(behavior = new InjectorBehavior(this));
    }

    public InjectorBehavior getInjectorBehavior() {
        return behavior;
    }

    public float getNextRotation() {
        rotation += 0.5f;
        if(rotation >= 360) rotation -= 360.0f;
        return rotation;
    }

    public void dropContents(Level level, BlockPos pos) {
        behavior.dropContents(level, pos);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag behaviorTag = new CompoundTag();
        behavior.write(behaviorTag, provider, false);
        tag.put("injector", behaviorTag);
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        behavior.read(tag.getCompound("injector"), provider, false);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        behavior.tick();
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
