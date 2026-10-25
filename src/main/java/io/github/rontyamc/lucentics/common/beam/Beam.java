package io.github.rontyamc.lucentics.common.beam;

import io.github.rontyamc.lucentics.common.beam.node.BeamNode;
import io.github.rontyamc.lucentics.common.dict.Colors;

import java.util.List;

public record Beam(List<BeamNode> nodes, Colors color) {}