package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder.BlockEntityFactory;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.blocks.LucenticsBlockEntityBuilder;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

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
}
