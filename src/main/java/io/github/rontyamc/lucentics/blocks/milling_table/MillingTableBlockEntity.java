package io.github.rontyamc.lucentics.blocks.milling_table;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MillingTableBlockEntity extends BaseBlockEntity {
    MillingTableBehavior behavior;
    private float rotation_item = 0.0f;
    private float rotation_table = 0.0f;

    public MillingTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {super(type, pos, state);}

    @Override
    public void attachBehavior(List<BlockEntityBehavior> behaviors) {
        behaviors.add(behavior = new MillingTableBehavior(this));
    }

    public MillingTableBehavior getMillingTableBehavior() {
        return behavior;
    }

    public float getNextRotationItem() {
        rotation_item += 0.5f;
        if (rotation_item >= 360) rotation_item -= 360.0f;
        return rotation_item;
    }

    public float getSpeed() {
        int processingContinue = level instanceof ServerLevel ? behavior.getProcessingContinue() : behavior.getProcessingContinueClient();
        int processingTimeMax = level instanceof ServerLevel ? behavior.getProcessingTimeMax() : behavior.getProcessingTimeMaxClient();

        return Math.clamp((float) processingContinue / processingTimeMax, 0.02f, 1.0f);
    }

    public float getNextRotationTable() {
        rotation_table += getSpeed() * 8;
        if (rotation_table >= 360) rotation_table -= 360.0f;
        return rotation_table;
    }

    public void dropContents(Level level, BlockPos pos) {
        behavior.dropContents(level, pos);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag behaviorTag = new CompoundTag();
        behavior.write(behaviorTag, provider, false);
        tag.put("milling_table", behaviorTag);
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        behavior.read(tag.getCompound("milling_table"), provider, false);
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
