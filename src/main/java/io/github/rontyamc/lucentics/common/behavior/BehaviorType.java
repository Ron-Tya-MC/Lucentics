package io.github.rontyamc.lucentics.common.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.rontyamc.lucentics.Lucentics;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class BehaviorType<T extends BlockEntityBehavior> {
    private static final Map<ResourceLocation, BehaviorType<?>> PROVIDERS = new HashMap<>();

    private final ResourceLocation id;
    private final List<BehaviorType<?>> parents;
    private final List<BehaviorType<?>> children = new ArrayList<>();

    private List<BehaviorType<?>> cachedAllParents;

    public BehaviorType(ResourceLocation id) {
        this(id, List.of());
    }

    public BehaviorType(String name) {
        this(toLocation(name), List.of());
    }

    public BehaviorType(String name, BehaviorType<?>... parents) {
        this(toLocation(name), List.of(parents));
    }

    public BehaviorType(ResourceLocation id, BehaviorType<?>... parents) {
        this(id, List.of(parents));
    }

    private BehaviorType(ResourceLocation id, List<BehaviorType<?>> parents) {
        this.id = id;
        this.parents = parents;

        for (BehaviorType<?> parent : parents) {
            parent.children.add(this);
        }

        register(id, this);
    }

    public static final Codec<BehaviorType<?>> CODEC = ResourceLocation.CODEC.comapFlatMap(
            id -> byId(id)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown behavior type: " + id)),
            BehaviorType::getId
    );

    public static final StreamCodec<ByteBuf, BehaviorType<?>> STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(
            id -> byId(id).orElseThrow(() -> new IllegalStateException("Unknown behavior type: " + id)),
            BehaviorType::getId
    );

    public ResourceLocation getId() {
        return this.id;
    }

    public List<BehaviorType<?>> getDirectParents() {
        return Collections.unmodifiableList(this.parents);
    }

    public List<BehaviorType<?>> getAllParents() {
        if (cachedAllParents == null) {
            List<BehaviorType<?>> parents = new ArrayList<>(this.parents);

            for (BehaviorType<?> parent : this.parents) {
                parents.addAll(parent.getAllParents());
            }

            cachedAllParents = Collections.unmodifiableList(parents);
        }
        return cachedAllParents;
    }

    public List<BehaviorType<?>> getDirectChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public List<BehaviorType<?>> getAllChildren() {
        List<BehaviorType<?>> children = new ArrayList<>(this.children);

        for (BehaviorType<?> child : this.children) {
            children.addAll(child.getAllChildren());
        }

        return children;
    }

    public List<ResourceLocation> getParentLocations() {
        return this.parents.stream().map(BehaviorType::getId).toList();
    }

    public static Optional<BehaviorType<?>> byId(ResourceLocation id) {
        return Optional.ofNullable(PROVIDERS.get(id));
    }

    private static void register(ResourceLocation id, BehaviorType<?> type) {
        BehaviorType<?> existing = PROVIDERS.putIfAbsent(id, type);
        if (existing != null) {
            // Idにはnamespaceを含めるので別mod同士で被ることはないと思うが、一応
            throw new IllegalStateException("Duplicate behavior id \"" + id + "\"");
        }
    }

    private static ResourceLocation toLocation(String name) {
        int i = name.indexOf(":");
        if (i >= 0) return ResourceLocation.tryParse(name);
        else return ResourceLocation.tryParse(Lucentics.MOD_ID + ":" + name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BehaviorType<?> other)) return false;
        return getId().equals(other.getId());
    }

    public boolean isKindOf(BehaviorType<?> other) {
        return this.equals(other) || this.getAllParents().contains(other);
    }

    public boolean isParentOf(BehaviorType<?> other) {
        return this.equals(other) || this.getAllChildren().contains(other);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
