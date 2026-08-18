package io.github.rontyamc.lucentics.common.behavior;

import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.beam.Beam;
import io.github.rontyamc.lucentics.common.beam.BeamNode;
import io.github.rontyamc.lucentics.common.dict.Colors;

import java.util.ArrayList;
import java.util.List;

public abstract class ReceiveBehavior extends BlockEntityBehavior {
    public static final BehaviorType<ReceiveBehavior> TYPE = new BehaviorType<>("receive");

    protected final List<Beam> trails = new ArrayList<>();

    public ReceiveBehavior(BaseBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }

    public void onBeamReceived(List<BeamNode> trail, Colors color) {
        trails.add(new Beam(List.copyOf(trail), color));
    }

    public List<Beam> getTrails() {
        return trails;
    }
}