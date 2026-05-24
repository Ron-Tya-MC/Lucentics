package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.RegistryEntry;
import io.github.rontyamc.lucentics.Lucentics;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LucenticsTabRegister {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Lucentics.MOD_ID);
    public static final Map<String, String> ITEM_CATEGORY = new HashMap<>();

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_MODE_TAB_INGREDIENTS = CREATIVE_MODE_TAB.register("lucentics_ingredients", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lucentics.ingredients"))
            .icon(LucenticsItemRegister.DUSK_BRICK::asStack)
            .displayItems(new RegistrateDisplayItemsGenerator(LucenticsTabRegister.CREATIVE_MODE_TAB_INGREDIENTS, "ingredients"))
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_MODE_TAB_BLOCKS = CREATIVE_MODE_TAB.register("lucentics_blocks", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lucentics.blocks"))
            .withTabsBefore(CREATIVE_MODE_TAB_INGREDIENTS.getKey())
            .icon(LucenticsBlockRegister.DUSK_BRICKS::asStack)
            .displayItems(new RegistrateDisplayItemsGenerator(LucenticsTabRegister.CREATIVE_MODE_TAB_BLOCKS, "blocks"))
            .build());

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TAB.register(bus);
    }

    public static class RegistrateDisplayItemsGenerator implements DisplayItemsGenerator {
        public final DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter;
        public final String registerType;

        public RegistrateDisplayItemsGenerator(DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter, String registerType) {
            this.tabFilter = tabFilter;
            this.registerType = registerType;
        }

        @Override
        public void accept(ItemDisplayParameters parameters, Output output) {
            List<Item> items = new LinkedList<>();
            items.addAll(collectItemsFromCategory(this.registerType));
            acceptAll(output, items);
        }

        // ブロックをアイテムとして取得
//        private List<Item> collectBlocksItems() {
//            List<Item> items = new ReferenceArrayList<>();
//            for (RegistryEntry<Block, Block> entry : Lucentics.registrate().getAll(Registries.BLOCK)) {
//                if (LucenticsRegistrate.alreadyInCreativeTab(entry, tabFilter))
//                    continue;
//                items.add(entry.get().asItem());
//            }
//            items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
//            return items;
//        }

        private List<Item> collectItemsFromCategory(String category) {
            List<Item> items = new ReferenceArrayList<>();
            Lucentics.LOGGER.info("Start");
            for (RegistryEntry<Item, Item> entry : Lucentics.registrate().getAll(Registries.ITEM)) {
                Lucentics.LOGGER.info("EntryAsItem -> {}", entry.get());
                Lucentics.LOGGER.info("Categories -> {}", ITEM_CATEGORY);
                Lucentics.LOGGER.info("EntryCategory -> {}", ITEM_CATEGORY.get(entry.get().toString()));
                Lucentics.LOGGER.info("Category -> {}", category);
                Lucentics.LOGGER.info("Accept -> {}", ITEM_CATEGORY.get(entry.get().toString()).equals(category));
                Lucentics.LOGGER.info(" --- ");

                if (LucenticsRegistrate.alreadyInCreativeTab(entry, tabFilter) || !ITEM_CATEGORY.get(entry.get().toString()).equals(category)) {
                    continue;
                }
                items.add(entry.get());
            }
            Lucentics.LOGGER.info("End");
            return items;
        }

        private static void acceptAll(Output output, List<Item> items) {
            for (Item item : items) {
                output.accept(item);
            }
        }
    }
}
