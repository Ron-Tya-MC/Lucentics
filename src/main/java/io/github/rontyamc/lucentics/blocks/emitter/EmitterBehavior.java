package io.github.rontyamc.lucentics.blocks.emitter;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.EmitBehavior;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.items.LensItem;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class EmitterBehavior extends EmitBehavior {
    public static final BehaviorType<EmitterBehavior> TYPE = new BehaviorType<>("emitter");

    public EmitterBehavior(BaseBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    @Override
    public Colors getColor() {
        EmitterBlockEntity be = (EmitterBlockEntity) blockEntity;
        return (!be.getContent().isEmpty() && be.getContent().getItem() instanceof LensItem) ? ((LensItem) be.getContent().getItem()).getColor() : Colors.SUNLIGHT;
    }

    @Override
    protected Direction getFacing() {
        return blockEntity.getBlockState().getValue(EmitterBlock.FACING);
    }

    @Override
    protected void triggerFullScan(ServerLevel level, Direction facing) {
        ItemStack lensStack = ((EmitterBlockEntity) blockEntity).inventory.getStackInSlot(0);
        Colors beamColor = lensStack.getItem() instanceof LensItem lens ? lens.getColor() : Colors.SUNLIGHT;
        scan(level, getPos(), facing, List.of(), beamColor);
        level.sendBlockUpdated(getPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
    }

    protected void syncColor(ServerLevel level) {
        ItemStack lensStack = ((EmitterBlockEntity) blockEntity).inventory.getStackInSlot(0);
        Colors beamColor = lensStack.getItem() instanceof LensItem lens ? lens.getColor() : Colors.SUNLIGHT;
        setColor(beamColor);
        level.sendBlockUpdated(getPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
    }

    @Override
    protected void lazyTick() {
        Level level = getWorld();
        if (level instanceof ServerLevel serverLevel) triggerFullScan(serverLevel, getFacing());
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putInt("beam_length", beamLength);
        nbt.putString("color", color.getSerializedName());
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        beamLength = nbt.getInt("beam_length");
        String colorName = nbt.getString("color");
        color = Colors.byName(colorName).orElse(Colors.SUNLIGHT);
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }
}
