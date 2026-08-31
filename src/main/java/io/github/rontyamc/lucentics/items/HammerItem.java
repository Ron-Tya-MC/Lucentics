package io.github.rontyamc.lucentics.items;

import net.minecraft.world.item.Item;

public class HammerItem extends Item {
    private final int cracksPerClick;
    private final int damagePerCrack;

    public HammerItem(Properties properties, int cracksPerClick, int damagePerCrack) {
        super(properties);
        this.cracksPerClick = cracksPerClick;
        this.damagePerCrack = damagePerCrack;
    }

    public int getCracksPerClick() {
        return cracksPerClick;
    }

    public int getDamagePerCrack() {
        return damagePerCrack;
    }
}
