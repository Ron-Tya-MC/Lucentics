package io.github.rontyamc.lucentics.registers;

import com.tterrag.registrate.util.entry.ItemEntry;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.items.HammerItem;
import io.github.rontyamc.lucentics.items.LensItem;
import io.github.rontyamc.lucentics.registers.LucenticsTabRegister.CategoryType;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class LucenticsItemRegister {
    public static final LucenticsRegistrate REGISTRATE = Lucentics.registrate();

    static {
        REGISTRATE.setCreativeTab(LucenticsTabRegister.CREATIVE_MODE_TAB_INGREDIENTS);
    }

    public static final ItemEntry<Item> DUSK_BRICK = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "dusk_brick", Item::new)
            .register();
    public static final ItemEntry<Item> RED_DUSK_BRICK = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "red_dusk_brick", Item::new)
            .register();
    public static final ItemEntry<Item> BLUE_DUSK_BRICK = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "blue_dusk_brick", Item::new)
            .register();
    public static final ItemEntry<Item> GREEN_DUSK_BRICK = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "green_dusk_brick", Item::new)
            .register();

    public static final ItemEntry<Item> LIGHT_COPPER = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "light_copper", Item::new)
            .register();
    public static final ItemEntry<Item> YELLOW_LIGHT_COPPER = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "yellow_light_copper", Item::new)
            .register();
    public static final ItemEntry<Item> MAGENTA_LIGHT_COPPER = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "magenta_light_copper", Item::new)
            .register();
    public static final ItemEntry<Item> LIGHT_BLUE_LIGHT_COPPER = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "light_blue_light_copper", Item::new)
            .register();

    public static final ItemEntry<Item> LENS_FRAME = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "lens_frame", Item::new)
            .register();

    public static final Map<Colors, ItemEntry<LensItem>> LENSES = registerLenses();

    private static Map<Colors, ItemEntry<LensItem>> registerLenses() {
        Map<Colors, ItemEntry<LensItem>> map = new EnumMap<>(Colors.class);
        for (Colors color : Colors.values()) {
            if (color == Colors.SUNLIGHT) continue; // SUNLIGHT色のレンズなんてないよ
            String name = color.getName() + "_lens";
            ItemEntry<LensItem> entry = REGISTRATE
                    .lucenticsItemBuilder(CategoryType.INGREDIENTS, name, p -> new LensItem(p.stacksTo(64), color))
                    .register();
            map.put(color, entry);
        }
        return map;
    }

    public static final ItemEntry<Item> DAWNSTONE_DUST = REGISTRATE.lucenticsItemBuilder(CategoryType.INGREDIENTS, "dawnstone_dust", Item::new)
            .register();

    public static final ItemEntry<HammerItem> COPPER_HAMMER = REGISTRATE
            .lucenticsItemBuilder(CategoryType.TOOLS, "copper_hammer",
                    p -> new HammerItem(p.durability(240), 2, 4))
            .properties(p -> p.attributes(
                    PickaxeItem.createAttributes(Tiers.STONE, 4, -2.8f))
                    .component(DataComponents.TOOL, new Tool(
                            List.of(Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_STONE_TOOL),
                                    Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, 5.0f)
                            ),
                            1.0f, 1))
            )
            .register();
    public static final ItemEntry<HammerItem> IRON_HAMMER = REGISTRATE
            .lucenticsItemBuilder(CategoryType.TOOLS, "iron_hammer",
                    p -> new HammerItem(p.durability(320), 3, 4))
            .properties(p -> p.attributes(
                    PickaxeItem.createAttributes(Tiers.IRON, 5, -3.0f))
                    .component(DataComponents.TOOL, new Tool(
                            List.of(Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_IRON_TOOL),
                                    Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, 7.0f)
                            ),
                            1.0f, 1))
            )
            .register();
    public static final ItemEntry<HammerItem> DIAMOND_HAMMER = REGISTRATE
            .lucenticsItemBuilder(CategoryType.TOOLS, "diamond_hammer",
                    p -> new HammerItem(p.durability(1920), 4, 4))
            .properties(p -> p.attributes(
                    PickaxeItem.createAttributes(Tiers.DIAMOND, 4, -2.8f))
                    .component(DataComponents.TOOL, new Tool(
                            List.of(Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
                                    Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, 9.0f)
                            ),
                            1.0f, 1))
            )
            .register();
    public static final ItemEntry<HammerItem> GOLDEN_HAMMER = REGISTRATE
            .lucenticsItemBuilder(CategoryType.TOOLS, "golden_hammer",
                    p -> new HammerItem(p.durability(80), 10, 10))
            .properties(p -> p.attributes(
                    PickaxeItem.createAttributes(Tiers.GOLD, 4, 0.0f))
                    .component(DataComponents.TOOL, new Tool(
                            List.of(Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_GOLD_TOOL),
                                    Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, 14.0f)
                            ),
                            1.0f, 1))
            )
            .register();

    public static void register() {
    }
}