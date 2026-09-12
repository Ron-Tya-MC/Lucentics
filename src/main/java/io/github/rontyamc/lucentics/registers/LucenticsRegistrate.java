package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder.BlockEntityFactory;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.LucenticsBlockEntityBuilder;
import io.github.rontyamc.lucentics.common.VirtualFluid;
import io.github.rontyamc.lucentics.common.datagen.builders.VirtualFluidBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LucenticsRegistrate extends AbstractRegistrate<LucenticsRegistrate> {
    /*
     * Derived from Create:
     * https://github.com/Creators-of-Create/Create
     *
     * Copyright (c) The Create Team / The Creators of Create
     * Licensed under the MIT License.
     */

    private static final Map<RegistryEntry<?, ?>, DeferredHolder<CreativeModeTab, CreativeModeTab>> TAB_LOOKUP = Collections.synchronizedMap(new IdentityHashMap<>());

    @Nullable
    protected DeferredHolder<CreativeModeTab, CreativeModeTab> currentTab;

    protected LucenticsRegistrate(String mod_id) {
        super(mod_id);
    }

    public static LucenticsRegistrate create(String mod_id) {
        return new LucenticsRegistrate(mod_id);
    }

    public static boolean alreadyInCreativeTab(RegistryEntry<?, ?> entry, DeferredHolder<CreativeModeTab, CreativeModeTab> tab) {
        return TAB_LOOKUP.get(entry) == tab;
    }

    public void setCreativeTab(DeferredHolder<CreativeModeTab, CreativeModeTab> tab) {
        currentTab = tab;
    }

    public DeferredHolder<CreativeModeTab, CreativeModeTab> getCreativeTab() {
        return currentTab;
    }

    @Override
    public LucenticsRegistrate registerEventListeners(IEventBus bus) {
        return super.registerEventListeners(bus);
    }

    @Override
    protected <R, T extends R> RegistryEntry<R, T> accept(String name, ResourceKey<? extends Registry<R>> type, Builder<R, T, ?, ?> builder, NonNullSupplier<? extends T> creator, NonNullFunction<DeferredHolder<R, T>, ? extends RegistryEntry<R, T>> entryFactory) {
        RegistryEntry<R, T> entry = super.accept(name, type, builder, creator, entryFactory);
        return entry;
    }

    public <T extends Block> BlockBuilder<T, LucenticsRegistrate> lucenticsBlockBuilder(String itemCategory, String name, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        LucenticsTabRegister.ITEM_CATEGORY.put(Lucentics.MOD_ID + ":" + name, itemCategory);

        return block(name, factory);
    }

    public <T extends Item> ItemBuilder<T, LucenticsRegistrate> lucenticsItemBuilder(String category, String name, NonNullFunction<Item.Properties, T> factory) {
        LucenticsTabRegister.ITEM_CATEGORY.put(Lucentics.MOD_ID + ":" + name, category);

        return item(name, factory);
    }

    @Override
    public <T extends BlockEntity> LucenticsBlockEntityBuilder<T, LucenticsRegistrate> blockEntity(String name, BlockEntityFactory<T> factory) {
        return blockEntity(self(), name, factory);
    }


    @Override
    public <T extends BlockEntity, P> LucenticsBlockEntityBuilder<T, P> blockEntity(P parent, String name,
                                                                                 BlockEntityFactory<T> factory) {
        return (LucenticsBlockEntityBuilder<T, P>) entry(name,
                (callback) -> LucenticsBlockEntityBuilder.create(this, parent, name, callback, factory));
    }

    public <T extends BaseFlowingFluid> FluidBuilder<T, LucenticsRegistrate> virtualFluid(String name,
                                                                                          FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<BaseFlowingFluid.Properties, T> sourceFactory,
                                                                                          NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
        return entry(name,
                c -> new VirtualFluidBuilder<>(self(), self(), name, c, ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_still"),
                        ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_flow"), typeFactory, sourceFactory, flowingFactory));
    }

    public <T extends BaseFlowingFluid> FluidBuilder<T, LucenticsRegistrate> virtualFluid(String name,
                                                                                       ResourceLocation still, ResourceLocation flow, FluidBuilder.FluidTypeFactory typeFactory,
                                                                                       NonNullFunction<BaseFlowingFluid.Properties, T> sourceFactory, NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
        return entry(name, c -> new VirtualFluidBuilder<>(self(), self(), name, c, still, flow, typeFactory, sourceFactory, flowingFactory));
    }

    public FluidBuilder<VirtualFluid, LucenticsRegistrate> virtualFluid(String name) {
        return entry(name,
                c -> new VirtualFluidBuilder<>(self(), self(), name, c,
                        ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_still"),
                        ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_flow"),
                        LucenticsRegistrate::defaultFluidType, VirtualFluid::createSource, VirtualFluid::createFlowing));
    }

    public FluidBuilder<VirtualFluid, LucenticsRegistrate> virtualFluid(String name, ResourceLocation still,
                                                                     ResourceLocation flow) {
        return entry(name, c -> new VirtualFluidBuilder<>(self(), self(), name, c, still, flow,
                LucenticsRegistrate::defaultFluidType, VirtualFluid::createSource, VirtualFluid::createFlowing));
    }

    public FluidBuilder<BaseFlowingFluid.Flowing, LucenticsRegistrate> standardFluid(String name) {
        return fluid(name, ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_still"),
                ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_flow"));
    }

    public FluidBuilder<BaseFlowingFluid.Flowing, LucenticsRegistrate> standardFluid(String name,
                                                                                  FluidBuilder.FluidTypeFactory typeFactory) {
        return fluid(name, ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_still"),
                ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_flow"),
                typeFactory);
    }

    public FluidBuilder<BaseFlowingFluid.Flowing, LucenticsRegistrate> waterLike(String name,
                                                                                     FluidBuilder.FluidTypeFactory typeFactory) {
        return fluid(name, ResourceLocation.withDefaultNamespace("block/water_still"),
                ResourceLocation.withDefaultNamespace("block/water_flow"), typeFactory);
    }

    public static FluidType defaultFluidType(FluidType.Properties properties, ResourceLocation stillTexture,
                                             ResourceLocation flowingTexture) {
        return new FluidType(properties) {
            @SuppressWarnings("removal")
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return stillTexture;
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return flowingTexture;
                    }
                });
            }
        };
    }

    public LucenticsRegistrate fluidItemRegister(String category, String name) {
        LucenticsTabRegister.ITEM_CATEGORY.put(Lucentics.MOD_ID + ":" + name + "_bucket", category);

        return this;
    }
}
