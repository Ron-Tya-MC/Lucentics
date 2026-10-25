package io.github.rontyamc.lucentics.common.util.tag_utils;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 複数のTagKey<Fluid>を纏め、必要な時にList<TagKey<Fluid>>かTagKey<Fluid>[]で取り出せる。
 */
public class FluidTagSet {
    private final List<TagKey<Fluid>> tags;

    /**
     * 列挙したTagKey<Fluid>全てを含むFluidTagSetを作成する。
     *
     * @param tags このFluidTagSetに登録したいTagKey<Fluid>の羅列。
     */
    @SafeVarargs
    public FluidTagSet(TagKey<Fluid>... tags) {
        this.tags = List.of(tags);
    }

    /**
     * 1つのFluidTagSetの内容に、列挙したTagKey<Fluid>全てを追加したFluidTagSetを新たに作成する。
     *
     * @param parent 基にしたいFluidTagSet。
     * @param additionalTags このFluidTagSetに追加したいTagKey<Fluid>の羅列。
     */
    @SafeVarargs
    public FluidTagSet(FluidTagSet parent, TagKey<Fluid>... additionalTags) {
        List<TagKey<Fluid>> combined = new ArrayList<>(parent.tags);
        Collections.addAll(combined, additionalTags);
        this.tags = Collections.unmodifiableList(combined);
    }

    /**
     * 複数のFluidTagSetの内容を全て含み、さらに列挙したTagKey<Fluid>全てを追加したFluidTagSetを新たに作成する。
     *
     * @param parents 基にしたいFluidTagSetのList。
     * @param additionalTags このFluidTagSetに追加したいTagKey<Fluid>の羅列。
     */
    @SafeVarargs
    public FluidTagSet(List<FluidTagSet> parents, TagKey<Fluid>... additionalTags) {
        List<TagKey<Fluid>> combined = new ArrayList<>();

        for (FluidTagSet parent : parents) {
            combined.addAll(parent.tags);
        }
        Collections.addAll(combined, additionalTags);

        this.tags = Collections.unmodifiableList(combined);
    }

    /**
     * このFluidTagSetに含まれるTagKey<Fluid>の全てを、リスト型で受け取る。
     * @return このFluidTagSetに含まれるTagKey<Fluid>のList。
     */
    public List<TagKey<Fluid>> tagList() {
        return this.tags;
    }

    /**
     * このFluidTagSetに含まれるTagKey<Fluid>の全てを、配列型で受け取る。
     * @return このFluidTagSetに含まれるTagKey<Fluid>の配列。
     */
    @SuppressWarnings("unchecked")
    public TagKey<Fluid>[] tags() {
        return this.tags.toArray(new TagKey[0]);
    }

    /**
     * 1つのFluidTagSetの内容に、列挙したTagKey<Fluid>全てを追加したTagKey<Fluid>の配列を返す。
     *
     * @param set 基にしたいFluidTagSet。
     * @param additionalTags 追加したいTagKey<Fluid>の羅列。
     *
     * @return 追加後のTagKey<Fluid>の配列。
     */
    @SafeVarargs
    public static TagKey<Fluid>[] merge(FluidTagSet set, TagKey<Fluid>... additionalTags) {
        List<TagKey<Fluid>> combined = new ArrayList<>(set.tags);
        Collections.addAll(combined, additionalTags);

        @SuppressWarnings("unchecked")
        TagKey<Fluid>[] result = combined.toArray(new TagKey[0]);
        return result;
    }

    /**
     * 複数のFluidTagSetの内容を全て含み、さらに列挙したTagKey<Fluid>全てを追加したTagKey<Fluid>の配列を返す。
     *
     * @param sets 基にしたいFluidTagSetのList。
     * @param additionalTags 追加したいTagKey<Fluid>の羅列。
     *
     * @return 追加後のTagKey<Fluid>の配列。
     */
    @SafeVarargs
    public static TagKey<Fluid>[] merge(List<FluidTagSet> sets, TagKey<Fluid>... additionalTags) {
        List<TagKey<Fluid>> combined = new ArrayList<>();
        for (FluidTagSet parent : sets) {
            combined.addAll(parent.tags);
        }
        Collections.addAll(combined, additionalTags);

        @SuppressWarnings("unchecked")
        TagKey<Fluid>[] result = combined.toArray(new TagKey[0]);
        return result;
    }

    /**
     * このFluidTagSetの内容に、列挙したTagKey<Fluid>全てを追加したTagKey<Fluid>の配列を返す。
     *
     * @param additionalTags 追加したいTagKey<Fluid>の羅列。
     *
     * @return 追加後のTagKey<Fluid>の配列。
     */
    @SafeVarargs
    public final TagKey<Fluid>[] mergeWith(TagKey<Fluid>... additionalTags) {
        List<TagKey<Fluid>> combined = new ArrayList<>(this.tags);
        Collections.addAll(combined, additionalTags);
        @SuppressWarnings("unchecked")
        TagKey<Fluid>[] result = combined.toArray(new TagKey[0]);
        return result;
    }
}
