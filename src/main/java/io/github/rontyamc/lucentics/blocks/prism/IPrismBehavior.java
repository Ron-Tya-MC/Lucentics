package io.github.rontyamc.lucentics.blocks.prism;

import io.github.rontyamc.lucentics.common.dict.Colors;

import java.util.Optional;

public interface IPrismBehavior {
    PrismBehavior getPrismBehavior();

    Optional<Colors> getRequiredColor();
}
