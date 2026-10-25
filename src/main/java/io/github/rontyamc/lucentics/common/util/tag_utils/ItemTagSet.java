package io.github.rontyamc.lucentics.common.util.tag_utils;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 複数のTagKey<Item>を纏め、必要な時にList<TagKey<Item>>かTagKey<Item>[]で取り出せる。
 */
public class ItemTagSet {
    private final List<TagKey<Item>> tags;

    /**
     * 列挙したTagKey<Item>全てを含むItemTagSetを作成する。
     *
     * @param tags このItemTagSetに登録したいTagKey<Item>の羅列。
     */
    @SafeVarargs
    public ItemTagSet(TagKey<Item>... tags) {
        this.tags = List.of(tags);
    }

    /**
     * 1つのItemTagSetの内容に、列挙したTagKey<Item>全てを追加したItemTagSetを新たに作成する。
     *
     * @param parent 基にしたいItemTagSet。
     * @param additionalTags このItemTagSetに追加したいTagKey<Item>の羅列。
     */
    @SafeVarargs
    public ItemTagSet(ItemTagSet parent, TagKey<Item>... additionalTags) {
        List<TagKey<Item>> combined = new ArrayList<>(parent.tags);
        Collections.addAll(combined, additionalTags);
        this.tags = Collections.unmodifiableList(combined);
    }

    /**
     * 複数のItemTagSetの内容を全て含み、さらに列挙したTagKey<Item>全てを追加したItemTagSetを新たに作成する。
     *
     * @param parents 基にしたいItemTagSetのList。
     * @param additionalTags このItemTagSetに追加したいTagKey<Item>の羅列。
     */
    @SafeVarargs
    public ItemTagSet(List<ItemTagSet> parents, TagKey<Item>... additionalTags) {
        List<TagKey<Item>> combined = new ArrayList<>();

        for (ItemTagSet parent : parents) {
            combined.addAll(parent.tags);
        }
        Collections.addAll(combined, additionalTags);

        this.tags = Collections.unmodifiableList(combined);
    }

    /**
     * このItemTagSetに含まれるTagKey<Item>の全てを、リスト型で受け取る。
     * @return このItemTagSetに含まれるTagKey<Item>のList。
     */
    public List<TagKey<Item>> tagList() {
        return this.tags;
    }

    /**
     * このItemTagSetに含まれるTagKey<Item>の全てを、配列型で受け取る。
     * @return このItemTagSetに含まれるTagKey<Item>の配列。
     */
    @SuppressWarnings("unchecked")
    public TagKey<Item>[] tags() {
        return this.tags.toArray(new TagKey[0]);
    }

    /**
     * 1つのItemTagSetの内容に、列挙したTagKey<Item>全てを追加したTagKey<Item>の配列を返す。
     *
     * @param set 基にしたいItemTagSet。
     * @param additionalTags 追加したいTagKey<Item>の羅列。
     *
     * @return 追加後のTagKey<Item>の配列。
     */
    @SafeVarargs
    public static TagKey<Item>[] merge(ItemTagSet set, TagKey<Item>... additionalTags) {
        List<TagKey<Item>> combined = new ArrayList<>(set.tags);
        Collections.addAll(combined, additionalTags);

        @SuppressWarnings("unchecked")
        TagKey<Item>[] result = combined.toArray(new TagKey[0]);
        return result;
    }

    /**
     * 複数のItemTagSetの内容を全て含み、さらに列挙したTagKey<Item>全てを追加したTagKey<Item>の配列を返す。
     *
     * @param sets 基にしたいItemTagSetのList。
     * @param additionalTags 追加したいTagKey<Item>の羅列。
     *
     * @return 追加後のTagKey<Item>の配列。
     */
    @SafeVarargs
    public static TagKey<Item>[] merge(List<ItemTagSet> sets, TagKey<Item>... additionalTags) {
        List<TagKey<Item>> combined = new ArrayList<>();
        for (ItemTagSet parent : sets) {
            combined.addAll(parent.tags);
        }
        Collections.addAll(combined, additionalTags);

        @SuppressWarnings("unchecked")
        TagKey<Item>[] result = combined.toArray(new TagKey[0]);
        return result;
    }

    /**
     * このItemTagSetの内容に、列挙したTagKey<Item>全てを追加したTagKey<Item>の配列を返す。
     *
     * @param additionalTags 追加したいTagKey<Item>の羅列。
     *
     * @return 追加後のTagKey<Item>の配列。
     */
    @SafeVarargs
    public final TagKey<Item>[] mergeWith(TagKey<Item>... additionalTags) {
        List<TagKey<Item>> combined = new ArrayList<>(this.tags);
        Collections.addAll(combined, additionalTags);
        @SuppressWarnings("unchecked")
        TagKey<Item>[] result = combined.toArray(new TagKey[0]);
        return result;
    }
}
