package io.github.rontyamc.lucentics.common.behavior;

import io.github.rontyamc.lucentics.common.GeneralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ConcurrentModificationException;

public abstract class BlockEntityBehavior {
    /*
     * Derived from Create:
     * https://github.com/Creators-of-Create/Create
     *
     * Copyright (c) The Create Team / The Creators of Create
     * Licensed under the MIT License.
     */
    public GeneralBlockEntity blockEntity;
    private int lazyTickRate;
    private int lazyTickCounter;

    public BlockEntityBehavior(GeneralBlockEntity be) {
        blockEntity = be;
        setLazyTickRate(10);
    }

    public void setLazyTickRate(int slowTickRate) {
        this.lazyTickRate = slowTickRate;
        this.lazyTickCounter = slowTickRate;
    }

    public abstract BehaviorType<?> getType();

    public void initialize() {

    }

    public void tick() {
        if (lazyTickCounter-- <= 0) {
            lazyTickCounter = lazyTickRate;
            lazyTick();
        }
    }

    private void lazyTick() {
    }

    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {

    }

    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {

    }

    /**
     * Called when isSafeNBT == true. Defaults to write()
     */
    public void writeSafe(CompoundTag nbt, HolderLookup.Provider registries) {
        write(nbt, registries, false);
    }

    public boolean isSafeNBT() {
        return false;
    }

    public void onBlockChanged(BlockState oldState) {

    }

    public void onNeighborChanged(BlockPos neighborPos) {

    }

    /**
     * Block destroyed or Chunk unloaded. Usually invalidates capabilities
     */
    public void unload() {
    }

    /**
     * Block destroyed or removed. Requires block to call ITE::onRemove
     */
    public void destroy() {
    }

    public BlockPos getPos() {
        return blockEntity.getBlockPos();
    }

    public Level getWorld() {
        return blockEntity.getLevel();
    }

    public static <T extends BlockEntityBehavior> T get(BlockGetter reader, BlockPos pos, BehaviorType<T> type) {
        BlockEntity be;
        try {
            be = reader.getBlockEntity(pos);
        } catch (ConcurrentModificationException e) {
            be = null;
        }
        return get(be, type);
    }

    public static <T extends BlockEntityBehavior> T get(BlockEntity be, BehaviorType<T> type) {
        if (be == null)
            return null;
        if (!(be instanceof GeneralBlockEntity ste))
            return null;
        return ste.getBehavior(type);
    }
}
