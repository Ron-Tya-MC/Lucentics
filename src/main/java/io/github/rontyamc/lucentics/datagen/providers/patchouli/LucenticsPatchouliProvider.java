package io.github.rontyamc.lucentics.datagen.providers.patchouli;

import com.google.gson.JsonObject;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.datagen.builders.patchouli.CategoryBuilder;
import io.github.rontyamc.lucentics.datagen.builders.patchouli.EntryBuilder;
import io.github.rontyamc.lucentics.datagen.providers.patchouli.pages.DevicesProvider;
import io.github.rontyamc.lucentics.datagen.providers.patchouli.pages.GettingStartedProvider;
import io.github.rontyamc.lucentics.datagen.providers.patchouli.pages.MaterialAndToolProvider;
import io.github.rontyamc.lucentics.datagen.providers.patchouli.pages.PrismProvider;
import io.github.rontyamc.lucentics.registers.LucenticsTabRegister;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LucenticsPatchouliProvider implements DataProvider {
    public static final String BOOK_ID = "luxicon";
    public static final String KEY_ROOT = "patchouli.";
    public static final String KEY_ROOT_LUCENTICS = KEY_ROOT + Lucentics.MOD_ID;
    private static final String MAIN_LANG = "en_us";
    private static final int VERSION = 1;

    private final PackOutput.PathProvider dataPath;
    private final PackOutput.PathProvider assetPath;
    private final List<CategoryBuilder> categories = new ArrayList<>();
    private final List<EntryBuilder> entries = new ArrayList<>();

    public LucenticsPatchouliProvider(PackOutput output) {
        this.dataPath = output.createPathProvider(PackOutput.Target.DATA_PACK, "patchouli_books");
        this.assetPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "patchouli_books");
    }

    private void register() {
        categories.clear();
        entries.clear();

        MacroProvider.register();

        GettingStartedProvider.build(this);
        DevicesProvider.build(this);
        PrismProvider.build(this);
        MaterialAndToolProvider.build(this);
    }

    public CategoryBuilder category(ResourceLocation id) {
        CategoryBuilder builder = new CategoryBuilder(id);
        categories.add(builder);
        return builder;
    }

    public CategoryBuilder category(String id) {
        CategoryBuilder builder = new CategoryBuilder(id);
        categories.add(builder);
        return builder;
    }

    public EntryBuilder entry(ResourceLocation category, String id) {
        EntryBuilder builder = new EntryBuilder(category, id);
        entries.add(builder);
        return builder;
    }

    public EntryBuilder entry(String category, String id) {
        EntryBuilder builder = new EntryBuilder(category, id);
        entries.add(builder);
        return builder;
    }

    public static String categoryKey(ResourceLocation id) {
        return KEY_ROOT + id.getNamespace() + ".category." + id.getPath().replace('/', '.');
    }

    public static String entryKey(ResourceLocation categoryId, ResourceLocation entryId) {
        return KEY_ROOT + entryId.getNamespace() + ".entry." + categoryId.getPath().replace('/', '.') + "." + entryId.getPath().replace('/', '.');
    }

    private void validate() {
        Set<ResourceLocation> categoryIds = new HashSet<>();
        for (CategoryBuilder category : categories) {
            if (!categoryIds.add(category.id())) {
                throw new IllegalStateException("Duplicate category: " + category.id());
            }
        }

        Set<ResourceLocation> entryIds = new HashSet<>();
        for (EntryBuilder entry : entries) {
            if (!categoryIds.contains(entry.category())) {
                throw new IllegalStateException("Entry \"" + entry.id() + "\" refers to unknown category: " + entry.category());
            }
            if (!entryIds.add(ResourceLocation.fromNamespaceAndPath(entry.id().getNamespace(),
                    entry.category().getNamespace() + "." + entry.category().getPath() + "/" + entry.id().getPath()))) {
                throw new IllegalStateException("Duplicate Entry: " + entry.id() + " in category: " + entry.category());
            }
        }
    }

    private JsonObject buildBook() {
        JsonObject json = new JsonObject();
        json.addProperty("name", KEY_ROOT_LUCENTICS + ".name");
        json.addProperty("landing_text", KEY_ROOT_LUCENTICS + ".landing_text");
        json.addProperty("use_resource_pack", true);
        json.addProperty("book_texture", "patchouli:textures/gui/book_red.png");
        json.addProperty("filler_texture", Lucentics.MOD_ID + ":textures/gui/patchouli/blank_page.png");
        json.addProperty("model", Lucentics.MOD_ID + ":luxicon");
        json.addProperty("version", VERSION);
        json.addProperty("creative_tab", LucenticsTabRegister.CREATIVE_MODE_TAB_TOOLS.getId().toString());
        json.addProperty("i18n", true);

        Map<String, String> macros = MacroProvider.getMacros();
        if (!macros.isEmpty()) {
            JsonObject macro = new JsonObject();
            for (Map.Entry<String, String> entry : macros.entrySet()) {
                macro.addProperty(entry.getKey(), entry.getValue());
            }
            json.add("macros", macro);
        }

        return json;
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        register();
        validate();

        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(DataProvider.saveStable(output, buildBook(),
                dataPath.json(Lucentics.path(BOOK_ID + "/book"))));

        for (CategoryBuilder category : categories) {
            futures.add(DataProvider.saveStable(output, category.build(),
                    assetPath.json(Lucentics.path(BOOK_ID + "/" + MAIN_LANG + "/categories/" + category.id().getPath()))));
        }

        for (EntryBuilder entry : entries) {
            futures.add(DataProvider.saveStable(output, entry.build(),
                    assetPath.json(Lucentics.path(BOOK_ID + "/" + MAIN_LANG + "/entries/" + entry.category().getPath() + "/" + entry.id().getPath()))));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Luxicon Provider";
    }
}
