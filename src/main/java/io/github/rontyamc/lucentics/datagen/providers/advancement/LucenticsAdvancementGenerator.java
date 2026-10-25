package io.github.rontyamc.lucentics.datagen.providers.advancement;

import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.common.dict.Colors;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import io.github.rontyamc.lucentics.datagen.builders.advancement.AdvancementBuilder;
import io.github.rontyamc.lucentics.datagen.builders.advancement.DisplayBuilder;
import io.github.rontyamc.lucentics.registers.LucenticsAdvancementEventRegister;
import io.github.rontyamc.lucentics.registers.LucenticsBlockRegister;
import io.github.rontyamc.lucentics.registers.LucenticsFluidRegister;
import io.github.rontyamc.lucentics.registers.LucenticsItemRegister;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LucenticsAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
    private Consumer<AdvancementHolder> consumer;

    public static AdvancementHolder ROOT,
        INJECTOR, DUSK_BRICK, DAWNSTONE, EMITTER,
        CRUSHING, DAWNSTONE_DUST, PRISM_RITUAL, ENGRAVING_TABLE,
        LIGHT_COPPER, RED_LENS, BLUE_LENS, GREEN_LENS,
        MILLING, MIXING, ASSEMBLING, DIAMOND_DUST,
        PRISM_DYEING, PATINA_LIQUID, OXIDIZE, PRISM_BALL,
        FOCUS_RING, GLIMMER_IRON, ADVANCED_EMITTER,
        YELLOW_LENS, MAGENTA_LENS, CYAN_LENS, COLORED_PRISM_BLANK,
        PRISM_IMPORTING, PRISM_EXPORTING, PRISM_IO, PRISM_COLLECTING,
        PRISM_ATTACK, PRISM_BREAK, PRISM_FLUID_PLACE, PRISM_FLUID_DRAIN,
        PRISM_PLACE;

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
        this.consumer = consumer;

        ROOT = createBuilder("root").displayInfo(
                DisplayBuilder.create(LucenticsItemRegister.LUXICON).title(Component.translatable("advancement.lucentics.root"))
                        .description(Component.translatable("advancement.lucentics.root.desc"))
                        .background(Lucentics.path("textures/gui/advancements/backgrounds/dusk_bricks.png"))
                        .showToast(false).announceToChat(false).build()
                ).addCriterion("root", CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()))).save(consumer);

        INJECTOR = obtain(LucenticsBlockRegister.INJECTOR, ROOT);
        DUSK_BRICK = obtain(LucenticsItemRegister.DUSK_BRICK, INJECTOR);
        DAWNSTONE = obtain(LucenticsBlockRegister.DAWNSTONE, INJECTOR);
        EMITTER = obtain(LucenticsBlockRegister.EMITTER, DUSK_BRICK);
        CRUSHING = createBuilder("crushing")
                .displayInfo(LucenticsItemRegister.COPPER_HAMMER)
                .on(LucenticsAdvancementEventRegister.CRUSHING_COMPLETED)
                .parent(DUSK_BRICK)
                .save(consumer);
        DAWNSTONE_DUST = obtain(LucenticsItemRegister.DAWNSTONE_DUST, DAWNSTONE);
        PRISM_RITUAL = obtain(LucenticsBlockRegister.PRISM_RITUAL, DAWNSTONE_DUST);
        ENGRAVING_TABLE = obtain(LucenticsBlockRegister.ENGRAVING_TABLE, PRISM_RITUAL);
        LIGHT_COPPER = createBuilder("light_copper")
                    .displayInfo(DisplayBuilder.create(LucenticsItemRegister.LIGHT_COPPER)
                            .type(AdvancementType.GOAL)
                            .title("advancement.lucentics.light_copper")
                            .description("advancement.lucentics.light_copper.desc")
                            .build()
                    )
                    .on(LucenticsAdvancementEventRegister.ENGRAVING_COMPLETED, Lucentics.path("engraving/light_copper"))
                    .parent(ENGRAVING_TABLE)
                    .save(consumer);
        RED_LENS = obtain(LucenticsItemRegister.LENSES.get(Colors.RED), LIGHT_COPPER);
        BLUE_LENS = obtain(LucenticsItemRegister.LENSES.get(Colors.BLUE), LIGHT_COPPER);
        GREEN_LENS = obtain(LucenticsItemRegister.LENSES.get(Colors.GREEN), LIGHT_COPPER);
        MILLING = createBuilder("milling")
                .displayInfo(LucenticsBlockRegister.MILLING_TABLE)
                .on(LucenticsAdvancementEventRegister.MILLING_COMPLETED)
                .parent(RED_LENS)
                .save(consumer);
        MIXING = createBuilder("mixing")
                .displayInfo(LucenticsBlockRegister.MIXING_TABLE)
                .on(LucenticsAdvancementEventRegister.MIXING_COMPLETED)
                .parent(BLUE_LENS)
                .save(consumer);
        ASSEMBLING = createBuilder("assembling")
                .displayInfo(LucenticsBlockRegister.ASSEMBLING_TABLE)
                .on(LucenticsAdvancementEventRegister.ASSEMBLING_COMPLETED)
                .parent(GREEN_LENS)
                .save(consumer);
        DIAMOND_DUST = obtain(LucenticsItemRegister.DIAMOND_DUST, MILLING);
        PRISM_DYEING = obtain(LucenticsBlockRegister.PRISM_DYEING, DIAMOND_DUST);
        PATINA_LIQUID = obtain(LucenticsFluidRegister.PATINA_LIQUID.getBucket().orElse(LucenticsItemRegister.NOTHING.asItem()), MIXING);
        OXIDIZE = createBuilder("oxidize")
                .displayInfo(() -> Blocks.OXIDIZED_COPPER)
                .on(LucenticsAdvancementEventRegister.DYEING_COMPLETED,
                        Lucentics.path("dyeing/oxidizing/exposed_copper"), Lucentics.path("dyeing/oxidizing/exposed_chiseled_copper"), Lucentics.path("dyeing/oxidizing/exposed_copper_bulb"),
                        Lucentics.path("dyeing/oxidizing/exposed_copper_door"), Lucentics.path("dyeing/oxidizing/exposed_copper_grate"), Lucentics.path("dyeing/oxidizing/exposed_cut_copper"),
                        Lucentics.path("dyeing/oxidizing/exposed_cut_copper_slab"), Lucentics.path("dyeing/oxidizing/exposed_cut_copper_stairs"), Lucentics.path("dyeing/oxidizing/exposed_cut_copper_trapdoor"),
                        Lucentics.path("dyeing/oxidizing/weathered_copper"), Lucentics.path("dyeing/oxidizing/weathered_chiseled_copper"), Lucentics.path("dyeing/oxidizing/weathered_copper_bulb"),
                        Lucentics.path("dyeing/oxidizing/weathered_copper_door"), Lucentics.path("dyeing/oxidizing/weathered_copper_grate"), Lucentics.path("dyeing/oxidizing/weathered_cut_copper"),
                        Lucentics.path("dyeing/oxidizing/weathered_cut_copper_slab"), Lucentics.path("dyeing/oxidizing/weathered_cut_copper_stairs"), Lucentics.path("dyeing/oxidizing/weathered_cut_copper_trapdoor"),
                        Lucentics.path("dyeing/oxidizing/oxidized_copper"), Lucentics.path("dyeing/oxidizing/oxidized_chiseled_copper"), Lucentics.path("dyeing/oxidizing/oxidized_copper_bulb"),
                        Lucentics.path("dyeing/oxidizing/oxidized_copper_door"), Lucentics.path("dyeing/oxidizing/oxidized_copper_grate"), Lucentics.path("dyeing/oxidizing/oxidized_cut_copper"),
                        Lucentics.path("dyeing/oxidizing/oxidized_cut_copper_slab"), Lucentics.path("dyeing/oxidizing/oxidized_cut_copper_stairs"), Lucentics.path("dyeing/oxidizing/oxidized_cut_copper_trapdoor"))
                .parent(PATINA_LIQUID)
                .save(consumer);
        PRISM_BALL = obtain(LucenticsItemRegister.PRISM_BALL, ASSEMBLING);
        FOCUS_RING = obtain(LucenticsItemRegister.FOCUS_RING, PRISM_BALL);
        GLIMMER_IRON = createBuilder("glimmer_iron")
                .displayInfo(DisplayBuilder.create(LucenticsItemRegister.GLIMMER_IRON)
                        .type(AdvancementType.GOAL)
                        .title("advancement.lucentics.glimmer_iron")
                        .description("advancement.lucentics.glimmer_iron.desc")
                        .build()
                )
                .on(LucenticsAdvancementEventRegister.ENGRAVING_COMPLETED, Lucentics.path("engraving/glimmer_iron"))
                .parent(FOCUS_RING)
                .save(consumer);
        ADVANCED_EMITTER = obtain(LucenticsBlockRegister.ADVANCED_EMITTER, GLIMMER_IRON);
        YELLOW_LENS = obtain(LucenticsItemRegister.LENSES.get(Colors.YELLOW), ADVANCED_EMITTER);
        MAGENTA_LENS = obtain(LucenticsItemRegister.LENSES.get(Colors.MAGENTA), ADVANCED_EMITTER);
        CYAN_LENS = obtain(LucenticsItemRegister.LENSES.get(Colors.CYAN), ADVANCED_EMITTER);

        PRISM_IMPORTING = obtain(LucenticsBlockRegister.PRISM_IMPORTING, DAWNSTONE_DUST);
        PRISM_EXPORTING = obtain(LucenticsBlockRegister.PRISM_EXPORTING, PRISM_IMPORTING);
        PRISM_IO = obtain(LucenticsBlockRegister.PRISM_IO, PRISM_EXPORTING);
        PRISM_COLLECTING = obtain(LucenticsBlockRegister.PRISM_COLLECTING, PRISM_IO);
        COLORED_PRISM_BLANK = createBuilder("colored_prism_blank")
                .displayInfo(LucenticsBlockRegister.COLORED_PRISM_BLANK.get(Colors.RED))
                .on(LucenticsAdvancementEventRegister.DYEING_COMPLETED,
                        Lucentics.path("dyeing/prism_blank_red"), Lucentics.path("dyeing/prism_blank_blue"),
                        Lucentics.path("dyeing/prism_blank_green"), Lucentics.path("dyeing/prism_blank_yellow"),
                        Lucentics.path("dyeing/prism_blank_magenta"), Lucentics.path("dyeing/prism_blank_cyan"),
                        Lucentics.path("dyeing/prism_blank_pink"), Lucentics.path("dyeing/prism_blank_lime"),
                        Lucentics.path("dyeing/prism_blank_light_blue"), Lucentics.path("dyeing/prism_blank_brown"),
                        Lucentics.path("dyeing/prism_blank_gray"), Lucentics.path("dyeing/prism_blank_light_gray"),
                        Lucentics.path("dyeing/prism_blank_orange"), Lucentics.path("dyeing/prism_blank_purple"),
                        Lucentics.path("dyeing/prism_blank_white"), Lucentics.path("dyeing/prism_blank_black"))
                .parent(PRISM_DYEING)
                .save(consumer);
        PRISM_ATTACK = obtain(LucenticsBlockRegister.PRISM_ATTACK, COLORED_PRISM_BLANK);
        PRISM_BREAK = obtain(LucenticsBlockRegister.PRISM_BREAK, COLORED_PRISM_BLANK);
        PRISM_FLUID_PLACE = obtain(LucenticsBlockRegister.PRISM_FLUID_PLACE, COLORED_PRISM_BLANK);
        PRISM_FLUID_DRAIN = obtain(LucenticsBlockRegister.PRISM_FLUID_DRAIN, COLORED_PRISM_BLANK);
        PRISM_PLACE = obtain(LucenticsBlockRegister.PRISM_PLACE, COLORED_PRISM_BLANK);
    }

    public AdvancementHolder obtain(ItemLike item, AdvancementHolder parent) {
        return createBuilder(ItemUtil.getId(item).getPath()).obtain(item).parent(parent).save(consumer);
    }

    public AdvancementHolder obtain(TagKey<Item> tag, Supplier<? extends ItemLike> displayItem, AdvancementHolder parent) {
        return createBuilder(tag.location().getPath()).obtain(displayItem, tag).parent(parent).save(consumer);
    }

    public AdvancementBuilder createBuilder(ResourceLocation id){
        return AdvancementBuilder.create(id);
    }

    public AdvancementBuilder createBuilder(String id){
        return createBuilder(Lucentics.path(id));
    }
}
