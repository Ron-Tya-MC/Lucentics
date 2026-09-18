package io.github.rontyamc.lucentics.common.beam.node;

import io.github.rontyamc.lucentics.common.dict.Colors;

import java.util.ArrayList;
import java.util.List;

public record NodeActivationContext(List<BeamNode> trail, BeamNode endpoint, int selfIndex, List<BeamNode> nodesAhead, Colors color) {
    public static NodeActivationContext create(List<BeamNode> trail, int selfIndex, BeamNode endpoint, Colors color) {
        List<BeamNode> ahead = new ArrayList<>(trail.subList(selfIndex + 1, trail.size()));
        if (endpoint != null) ahead.add(endpoint);
        return new NodeActivationContext(trail, endpoint, selfIndex, ahead, color);
    }
}
