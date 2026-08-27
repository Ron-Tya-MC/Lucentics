package io.github.rontyamc.lucentics.blocks.emitter;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.behavior.EmitBehavior;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.items.LensItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;

public class EmitterBehavior extends EmitBehavior implements Clearable {
    public static final BehaviorType<EmitterBehavior> TYPE = new BehaviorType<>("emitter");

    private ItemStack lensContainer;
    private Supplier<Integer> maxStackSize;
    public EmitterIHandler iHandler;

    public EmitterBehavior(BaseBlockEntity be) {
        super(be);

        maxStackSize = () -> 1;
        iHandler = new EmitterIHandler(this);
        clearContent();
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    public ItemStack getLensContainer() {return lensContainer == null ? ItemStack.EMPTY : lensContainer;}

    public void clearContent() {
        lensContainer = ItemStack.EMPTY;
    }

    @Override
    public Colors getColor() {
        return (!getLensContainer().isEmpty() && getLensContainer().getItem() instanceof LensItem) ? ((LensItem) getLensContainer().getItem()).getColor() : Colors.SUNLIGHT;
    }

    @Override
    protected Direction getFacing() {
        return blockEntity.getBlockState().getValue(EmitterBlock.FACING);
    }

    @Override
    protected void triggerFullScan(ServerLevel level, Direction facing) {
        Colors beamColor = getLensContainer().getItem() instanceof LensItem lens ? lens.getColor() : Colors.SUNLIGHT;
        scan(level, getPos(), facing, List.of(), beamColor);
        level.sendBlockUpdated(getPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
    }

    protected void syncColor(ServerLevel level) {
        Colors beamColor = getLensContainer().getItem() instanceof LensItem lens ? lens.getColor() : Colors.SUNLIGHT;
        setColor(beamColor);
        level.sendBlockUpdated(getPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
    }

    @Override
    public void tick() {
        Level level = getWorld();
        if (!(level instanceof ServerLevel serverLevel)) return;
        
        if (!isPowered(serverLevel)) setStopBeam(true);
        super.tick();
    }

    @Override
    protected void lazyTick() {
        Level level = getWorld();
        if (level instanceof ServerLevel serverLevel && isPowered(serverLevel)) triggerFullScan(serverLevel, getFacing());
    }

    public int getRemainingSpace() {
        return getLensContainer().isEmpty() ? maxStackSize.get() : 0;
    }

    public int getSlotLimit() {
        return 1;
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!getLensContainer().isEmpty()) return stack;
        if (!(stack.getItem() instanceof LensItem)) return stack;

        int insertCount = maxStackSize.get();
        ItemStack returnStack = stack.copyWithCount(stack.getCount() - insertCount);

        if (!simulate) {
            lensContainer = stack.copyWithCount(insertCount);
            blockEntity.updated();
        }

        return returnStack;
    }

    public ItemStack extract(int amount, boolean simulate) {
        if (getLensContainer().isEmpty()) return ItemStack.EMPTY;

        ItemStack copyStack = getLensContainer().copy();
        ItemStack extracted = copyStack.split(amount);

        if (!simulate) {
            lensContainer = copyStack;
            blockEntity.updated();
        }

        return extracted;
    }

    public void dropContents(Level level, BlockPos pos) {
        Vec3 vec = getCenter(pos);
        ItemStack stack = getLensContainer();

        if (!stack.isEmpty()) Containers.dropItemStack(level, vec.x, vec.y, vec.z, stack);
        clearContent();
    }

    private boolean isPowered(ServerLevel level) { return level.hasNeighborSignal(getPos()); }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if (!getLensContainer().isEmpty()) nbt.put("lens", getLensContainer().save(registries, new CompoundTag()));
        nbt.putInt("beam_length", beamLength);
        nbt.putString("color", color.getSerializedName());
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        lensContainer = nbt.contains("lens") ? ItemStack.parse(registries, nbt.getCompound("lens")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        beamLength = nbt.getInt("beam_length");
        String colorName = nbt.getString("color");
        color = Colors.byName(colorName).orElse(Colors.SUNLIGHT);
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }
}
