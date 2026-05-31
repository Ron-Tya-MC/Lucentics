package io.github.rontyamc.lucentics.common.behavior;

public class BehaviorType<T extends BlockEntityBehavior> {
    private String type;

    public BehaviorType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
