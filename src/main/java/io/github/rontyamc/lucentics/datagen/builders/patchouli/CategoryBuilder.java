package io.github.rontyamc.lucentics.datagen.builders.patchouli;

import com.google.gson.JsonObject;
import io.github.rontyamc.lucentics.Lucentics;
import io.github.rontyamc.lucentics.datagen.providers.patchouli.LucenticsPatchouliProvider;
import io.github.rontyamc.lucentics.common.util.ItemUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CategoryBuilder {
    private final ResourceLocation id;
    private ResourceLocation icon = Lucentics.path("nothing");
    @Nullable
    private ResourceLocation parent = null;
    private String flag = "";
    private int sortnum = 0;
    private boolean secret = false;

    public CategoryBuilder(ResourceLocation id) {
        this.id = id;
    }

    public CategoryBuilder(String id) {
        this.id = Lucentics.path(id);
    }

    public ResourceLocation id() {
        return id;
    }

    public CategoryBuilder icon(Supplier<? extends ItemLike> item) {
        this.icon = ItemUtil.getId(item);
        return this;
    }

    public CategoryBuilder parent(ResourceLocation parentId) {
        this.parent = parentId;
        return this;
    }

    public CategoryBuilder parent(String parentId) {
        this.parent = Lucentics.path(parentId);
        return this;
    }

    public CategoryBuilder flag(String flag) {
        this.flag = flag;
        return this;
    }

    public CategoryBuilder sortnum(int sortnum) {
        this.sortnum = sortnum;
        return this;
    }

    public CategoryBuilder secret(boolean secret) {
        this.secret = secret;
        return this;
    }

    public JsonObject build() {
        String prefix = LucenticsPatchouliProvider.categoryKey(id);

        JsonObject json = new JsonObject();
        json.addProperty("name", prefix + ".name");
        json.addProperty("description", prefix + ".description");
        json.addProperty("icon", icon.toString());
        if (parent != null) json.addProperty("parent", parent.toString());
        if (flag != null && !flag.isEmpty()) json.addProperty("flag", flag);
        json.addProperty("sortnum", sortnum);
        if (secret) json.addProperty("secret", true);
        return json;
    }
}
