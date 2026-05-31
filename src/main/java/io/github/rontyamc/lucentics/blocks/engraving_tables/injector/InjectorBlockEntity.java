package io.github.rontyamc.lucentics.blocks.engraving_tables.injector;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.GeneralBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BlockEntityBehavior;
import io.github.rontyamc.lucentics.common.recipe.LucenticsRecipes;
import io.github.rontyamc.lucentics.registers.LucenticsBlockEntityRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class InjectorBlockEntity extends GeneralBlockEntity {
    InjectorBlockBehavior injectorBehavior;

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
            if (!inventory.getStackInSlot(0).isEmpty()) {
                notifyInserted();
            }
        }
    };
    private float rotation;

    protected final ContainerData data;
    private int processingTime = -1;
    private boolean recipeCheck = false;

    public InjectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(LucenticsBlockEntityRegister.INJECTOR.get(), pos, state);
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return processingTime;
            }

            @Override
            public void set(int index, int value) {
                processingTime = value;
            }
            @Override
            public int getCount() {
                return 1;
            }
        };
    }

    @Override
    public void attachBehavior(List<BlockEntityBehavior> behaviors) {
        behaviors.add(injectorBehavior = new InjectorBlockBehavior(this));
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if(rotation >= 360) {
            rotation -= 360.0f;
        }
        return rotation;
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void dropContents() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i=0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("Item", inventory.serializeNBT(provider));
        tag.putInt("processingTime", processingTime);

        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        inventory.deserializeNBT(provider, tag.getCompound("Item"));
        processingTime = tag.getInt("processingTime");
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!isIdol()) {
            if (hasRecipe()) {
                craftingProcess();
                setChanged(level, pos, state);

                if (hasCraftingFinished()) {
                    craftItem();
                    setIdol();
                }

            }
            else {
                setIdol();
            }
        }
        else  {
            if (this.recipeCheck) {
                if (hasRecipe()) {
                    processingTime = 100;
                }
            }
        }
    }

    private void craftItem() {
        Optional<RecipeHolder<InjectorRecipe>> recipe = getCurrentRecipe();
        Lucentics.LOGGER.info("No recipe found -> {}", recipe.get());
        Lucentics.LOGGER.info("No recipe found -> {}", recipe.get().value());
        Lucentics.LOGGER.info("No recipe found -> {}", recipe.get().value().output());
        ItemStack output = recipe.get().value().output();

        //inventory.extractItem(0, recipe.get().value().inputItem().getItems()[0].getCount(), false);
        inventory.extractItem(0, 1, false);
        inventory.setStackInSlot(0, output);
    }

    private void setIdol() {
        processingTime = -1;
    }

    private boolean isIdol() {
        return processingTime == -1;
    }

    private boolean hasCraftingFinished() {
        return processingTime == 0;
    }

    private void craftingProcess() {
        processingTime--;
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<InjectorRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }
        return getDaylight(this.level, this.worldPosition) >= 7;
    }

    private static int getDaylight(Level level, BlockPos pos) {
        int i = level.getBrightness(LightLayer.SKY, pos) - level.getSkyDarken();
        float f = level.getSunAngle(1.0F);

        if (i > 0) {
            float f1 = f < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
            f += (f1 - f) * 0.2F;
            i = Math.round((float)i * Mth.cos(f));
        }

        i = Mth.clamp(i, 0, 15);
        return i;
    }

    private Optional<RecipeHolder<InjectorRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager().getRecipeFor(LucenticsRecipes.INJECTOR_TYPE.get(), new InjectorRecipeInput(inventory.getStackInSlot(0)), level);
    }

    private void notifyInserted() {
        this.recipeCheck = true;
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
