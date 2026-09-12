package io.github.rontyamc.lucentics.common.util;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import io.github.rontyamc.lucentics.Lucentics;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

public final class ModelUtil {
    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simpleBlockState(String folder) {
        return (context, provider) -> provider.simpleBlock(
                context.getEntry(),
                provider.models().getExistingFile(
                        Lucentics.defaultLocation(folder.isEmpty() ? "block/" + context.getName() : "block/" + folder + "/" + context.getName()))
        );
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> cubeAllBlockState(String folder) {
        return (context, provider) -> provider.simpleBlock(context.getEntry(),
                provider.models().cubeAll(context.getName(),
                        Lucentics.defaultLocation(folder.isEmpty() ? "block/" + context.getName() : "block/" + folder + "/" + context.getName())));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> cubeColumnBlockState(String side_path, String end_path) {
        return (context, provider) -> provider.simpleBlock(context.getEntry(),
                provider.models().cubeColumn(context.getName(),
                        Lucentics.defaultLocation("block/" + side_path),
                        Lucentics.defaultLocation("block/" + end_path)));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> horizontalDirectionalBlockState(String folder) {
        return (context, provider) -> {
            var model = provider.models().getExistingFile(Lucentics.defaultLocation(folder.isEmpty() ? "block/" + context.getName() : "block/" + folder + "/" + context.getName()));

            provider.getVariantBuilder(context.getEntry())
                    .forAllStates(state -> {
                        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
                        return ConfiguredModel.builder()
                                .modelFile(model)
                                .rotationY((int) facing.toYRot())
                                .build();
                    });
        };
    }

    public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> generatedItemModel(String folder) {
        return (context, provider) -> provider.generated(
                context::getEntry,
                Lucentics.defaultLocation(folder.isEmpty() ? "item/" + context.getName() : "item/" + folder + "/" + context.getName())
        );
    }
}
