package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import io.github.rontyamc.lucentics.Lucentics;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public class LucenticsRegistrate extends AbstractRegistrate<LucenticsRegistrate> {
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

    @Nullable
    public LucenticsRegistrate setCreativeTab(DeferredHolder<CreativeModeTab, CreativeModeTab> tab) {
        currentTab = tab;
        return self();
    }

    public DeferredHolder<CreativeModeTab, CreativeModeTab> getCreativeTab() {
        return currentTab;
    }

    @Override
    protected <R, T extends R> RegistryEntry<R, T> accept(String name, ResourceKey<? extends Registry<R>> type, Builder<R, T, ?, ?> builder, NonNullSupplier<? extends T> creator, NonNullFunction<DeferredHolder<R, T>, ? extends RegistryEntry<R, T>> entryFactory) {
        RegistryEntry<R, T> entry = super.accept(name, type, builder, creator, entryFactory);
        if (currentTab != null)
            TAB_LOOKUP.put(entry, currentTab);
        return entry;
    }


    public <T extends Block> BlockBuilder<T, LucenticsRegistrate> block(String itemCategory , String name, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        LucenticsTabRegister.ITEM_CATEGORY.put(Lucentics.MOD_ID + ":" + name, itemCategory);

        return block(self(), name, factory);
    }
}
