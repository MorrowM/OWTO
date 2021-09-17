package com.battlepandas.owto;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AirStatus {
    final static int MIN_AIR = -20;
    public int respiration;
    private int ticks;

    public AirStatus(Player player) {
        this(player.getRemainingAir(), getPlayerRespiration(player));
    }

    public AirStatus(int ticks, int respiration) {
        this.ticks = ticks * (respiration + 1);
        this.respiration = respiration;
    }

    private static int clamp(int min, int max, int val) {
        return (Integer.min(max, Integer.max(min, val)));
    }

    public static int getPlayerRespiration(PlayerInventory playerInv) {
        ItemStack helmet = playerInv.getHelmet();
        return helmet == null ? 0 : helmet.getEnchantmentLevel(Enchantment.OXYGEN);
    }

    public static int getPlayerRespiration(Player player) {
        PlayerInventory playerInv = player.getInventory();
        return getPlayerRespiration(playerInv);
    }

    public int getTicks() {
        return ticks / (respiration + 1);
    }

    public int addDelta(int delta, Player player) {
        this.respiration = getPlayerRespiration(player);

        if (delta >= 0) {
            this.ticks += delta * (respiration + 1);
        } else {
            this.ticks += delta;
        }

        this.ticks = clamp(MIN_AIR * (respiration + 1)
                , player.getMaximumAir() * (respiration + 1)
                , this.ticks);

        return this.ticks / (respiration + 1);
    }
}
