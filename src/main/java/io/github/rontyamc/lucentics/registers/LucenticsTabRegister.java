package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import io.github.rontyamc.lucentics.Lucentics;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LucenticsTabRegister {
    /*
     * Derived from Create:
     * https://github.com/Creators-of-Create/Create
     *
     * Copyright (c) The Create Team / The Creators of Create
     * Licensed under the MIT License.
     */

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Lucentics.MOD_ID);
    public static final Map<String, String> ITEM_CATEGORY = new HashMap<>();

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_MODE_TAB_INGREDIENTS = CREATIVE_MODE_TAB_REGISTER.register("lucentics_" + CategoryType.INGREDIENTS, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lucentics.ingredients"))
            .icon(LucenticsItemRegister.DUSK_BRICK::asStack)
            .displayItems(new RegistrateDisplayItemsGenerator(LucenticsTabRegister.CREATIVE_MODE_TAB_INGREDIENTS, CategoryType.INGREDIENTS))
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_MODE_TAB_BLOCKS = CREATIVE_MODE_TAB_REGISTER.register("lucentics_" + CategoryType.BLOCKS, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lucentics.blocks"))
            .withTabsBefore(CREATIVE_MODE_TAB_INGREDIENTS.getKey())
            .icon(LucenticsBlockRegister.DUSK_BRICKS::asStack)
            .displayItems(new RegistrateDisplayItemsGenerator(LucenticsTabRegister.CREATIVE_MODE_TAB_BLOCKS, CategoryType.BLOCKS))
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_MODE_TAB_MACHINES = CREATIVE_MODE_TAB_REGISTER.register("lucentics_" + CategoryType.MACHINES, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lucentics.machines"))
            .withTabsBefore(CREATIVE_MODE_TAB_BLOCKS.getKey())
            .icon(LucenticsBlockRegister.INJECTOR::asStack)
            .displayItems(new RegistrateDisplayItemsGenerator(LucenticsTabRegister.CREATIVE_MODE_TAB_MACHINES, CategoryType.MACHINES))
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_MODE_TAB_PRISMS = CREATIVE_MODE_TAB_REGISTER.register("lucentics_" + CategoryType.PRISMS, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lucentics.prisms"))
            .withTabsBefore(CREATIVE_MODE_TAB_MACHINES.getKey())
            .icon(LucenticsBlockRegister.PRISM_RITUAL::asStack)
            .displayItems(new RegistrateDisplayItemsGenerator(LucenticsTabRegister.CREATIVE_MODE_TAB_PRISMS, CategoryType.PRISMS))
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_MODE_TAB_TOOLS = CREATIVE_MODE_TAB_REGISTER.register("lucentics_" + CategoryType.TOOLS, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lucentics.tools"))
            .withTabsBefore(CREATIVE_MODE_TAB_PRISMS.getKey())
            .icon(LucenticsItemRegister.COPPER_HAMMER::asStack)
            .displayItems(new RegistrateDisplayItemsGenerator(LucenticsTabRegister.CREATIVE_MODE_TAB_TOOLS, CategoryType.TOOLS))
            .build());

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TAB_REGISTER.register(bus);
    }

    public record RegistrateDisplayItemsGenerator(DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter,
                                                  String registerType) implements DisplayItemsGenerator {

        private static Function<Item, ItemStack> makeStackFunc() {
                Map<Item, Function<Item, ItemStack>> factories = new Reference2ReferenceOpenHashMap<>();

                Map<ItemProviderEntry<?, ?>, Function<Item, ItemStack>> simpleFactories = Map.of(
                        /*
                         メモ
                         LucenticsItemRegister.EXAMPLE, item -> {
                            ItemStack stack = new ItemStack(item);
                            stack.set(SomeComponents.SOME_COMPONENT, someValue);
                           return stack;
                        }
                        */
                );

                simpleFactories.forEach((entry, factory) -> {
                    factories.put(entry.asItem(), factory);
                });

                return item -> {
                    Function<Item, ItemStack> factory = factories.get(item);
                    if (factory != null) {
                        return factory.apply(item);
                    }
                    return new ItemStack(item);
                };
            }

            private static Function<Item, TabVisibility> makeVisibilityFunc() {
                Map<Item, TabVisibility> visibilities = new Reference2ObjectOpenHashMap<>();

                Map<ItemProviderEntry<?, ?>, TabVisibility> defineVisibilities = Map.of(
                        // メモ
                        // LucenticsItemRegister.EXAMPLE, TabVisibility.SEARCH_TAB_ONLY
                );

                defineVisibilities.forEach((entry, factory) -> {
                    visibilities.put(entry.asItem(), factory);
                });

                return item -> {
                    TabVisibility visibility = visibilities.get(item);
                    if (visibility != null) {
                        return visibility;
                    }
                    return TabVisibility.PARENT_AND_SEARCH_TABS;
                };
            }

            @Override
            public void accept(ItemDisplayParameters parameters, Output output) {
                Function<Item, ItemStack> stackFunc = makeStackFunc();
                Function<Item, TabVisibility> visibilityFunc = makeVisibilityFunc();

                List<Item> items = new LinkedList<>(collectItemsFromCategory(this.registerType));

                for (Item item : items) {
                    output.accept(stackFunc.apply(item), visibilityFunc.apply(item));
                }
            }

            // 使うかも
            private List<Item> collectBlocksItems() {
                List<Item> items = new ReferenceArrayList<>();
                for (RegistryEntry<Block, Block> entry : Lucentics.registrate().getAll(Registries.BLOCK)) {
                    if (LucenticsRegistrate.alreadyInCreativeTab(entry, tabFilter))
                        continue;
                    items.add(entry.get().asItem());
                }
                items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
                return items;
            }

            private List<Item> collectItemsFromCategory(String category) {
                List<Item> items = new ReferenceArrayList<>();
                for (RegistryEntry<Item, Item> entry : Lucentics.registrate().getAll(Registries.ITEM)) {
                    if (LucenticsRegistrate.alreadyInCreativeTab(entry, tabFilter) || !ITEM_CATEGORY.get(entry.get().toString()).equals(category)) {
                        continue;
                    }
                    items.add(entry.get());
                }
                return items;
            }
        }

    public static class CategoryType {
        public static final String NONE = "none";
        public static final String INGREDIENTS = "ingredients";
        public static final String BLOCKS = "blocks";
        public static final String MACHINES = "machines";
        public static final String PRISMS = "prisms";
        public static final String TOOLS = "tools";

        private CategoryType() {
        }
    }
}