package io.github.rontyamc.lucentics.datagen.builders.patchouli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.client.patchouli.*;
import io.github.rontyamc.lucentics.datagen.providers.patchouli.LucenticsPatchouliProvider;
import io.github.rontyamc.lucentics.common.dict.Warns;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EntryBuilder {
    private final ResourceLocation id;
    private final ResourceLocation category;
    private ResourceLocation icon = ResourceLocation.withDefaultNamespace("writable_book");
    @Nullable
    protected ResourceLocation advancement = null;
    private String flag = "";
    private boolean priority = false;
    private boolean secret = false;
    private boolean readByDefault = false;
    private int sortnum = 0;
    @Nullable
    protected ResourceLocation turnIn = null;
    int entryColor = -1;
    private final List<BasePage<?>> pages = new ArrayList<>();

    public EntryBuilder(ResourceLocation category, ResourceLocation id) {
        this.category = category;
        this.id = id;
    }

    public EntryBuilder(ResourceLocation category, String id) {
        this.category = category;
        this.id = Lucentics.path(id);
    }

    public EntryBuilder(String category, ResourceLocation id) {
        this.category = Lucentics.path(category);
        this.id = id;
    }

    public EntryBuilder(String category, String id) {
        this.category = Lucentics.path(category);
        this.id = Lucentics.path(id);
    }

    public ResourceLocation category() {
        return category;
    }

    public ResourceLocation id() {
        return id;
    }

    public EntryBuilder icon(Supplier<? extends ItemLike> item) {
        this.icon = ItemUtil.getId(item);
        return this;
    }

    public EntryBuilder advancement(ResourceLocation advancement) {
        this.advancement = advancement;
        return this;
    }

    public EntryBuilder advancement(String advancement) {
        this.advancement = Lucentics.path(advancement);
        return this;
    }

    public EntryBuilder advancement(AdvancementHolder advancement) {
        this.advancement = advancement.id();
        return this;
    }

    public EntryBuilder flag(String flag) {
        this.flag = flag;
        return this;
    }

    public EntryBuilder priority(boolean priority) {
        this.priority = priority;
        return this;
    }

    public EntryBuilder secret(boolean secret) {
        this.secret = secret;
        return this;
    }

    public EntryBuilder readByDefault(boolean readByDefault) {
        this.readByDefault = readByDefault;
        return this;
    }

    public EntryBuilder sortnum(int sortnum) {
        this.sortnum = sortnum;
        return this;
    }

    public EntryBuilder turnIn(ResourceLocation turnIn) {
        this.turnIn = turnIn;
        return this;
    }

    public EntryBuilder entryColor(int entryColor) {
        this.entryColor = entryColor;
        return this;
    }

    public EntryBuilder page(BasePage<?> page) {
        pages.add(page);
        page.setAnchor("anchor" + pages.size());
        return this;
    }

    public EntryBuilder text(String key, boolean titled) {
        if (pages.isEmpty() && titled) {
            Warns.FIRST_PAGE_CANNOT_BE_TITLED.cast(id);
            titled = false;
        }
        return page(new TextPage(key).titled(titled));
    }

    public EntryBuilder text(String key) {
        text(key, false);
        return this;
    }

    public EntryBuilder image(ResourceLocation image) {
        return page(new ImagePage().addImage(image));
    }

    public EntryBuilder crafting(ResourceLocation recipe) {
        return page(new CraftingPage(recipe));
    }

    public EntryBuilder spotlight(ItemLike item) {
        return page(new SpotlightPage(item.asItem()));
    }

    public JsonObject build() {
        String prefix = LucenticsPatchouliProvider.entryKey(category, id);

        JsonObject json = new JsonObject();
        json.addProperty("name", prefix + ".name");
        json.addProperty("category", category.toString());
        json.addProperty("icon", icon.toString());
        if (advancement != null) json.addProperty("advancement", advancement.toString());
        if (flag != null && !flag.isEmpty()) json.addProperty("flag", flag);
        if (priority) json.addProperty("priority", true);
        if (secret) json.addProperty("secret", true);
        if (readByDefault) json.addProperty("read_by_default", true);
        json.addProperty("sortnum", sortnum);
        if (turnIn != null) json.addProperty("turnin", turnIn.toString());
        if (entryColor >= 0) json.addProperty("entry_color", Integer.toHexString(entryColor));

        JsonArray array = new JsonArray();
        for (BasePage<?> page : pages) array.add(page.toJson(prefix));
        json.add("pages", array);
        return json;
    }
}
