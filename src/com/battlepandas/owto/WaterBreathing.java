package com.battlepandas.owto;

import org.bukkit.Material;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WaterBreathing implements Listener {
    public final static int AIR_REGAIN = 5;
    private final static PotionEffect HASTE =
            new PotionEffect(PotionEffectType.FAST_DIGGING, 40, 5, true, false, false);
    private final static PotionEffect FATIGUE =
            new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 0, true, false, false);
    private final static PotionEffect SLOWNESS =
            new PotionEffect(PotionEffectType.SLOW, 40, 0, true, false, false);
    private final static PotionEffect SPEED =
            new PotionEffect(PotionEffectType.SPEED, 40, 0, true, false, false);

    private final Map<LivingEntity, AirStatus> playerAirs;

    public WaterBreathing(OWTO plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.playerAirs = new HashMap<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    int delta = calculateDelta(player);
                    AirStatus currentAir = getCurrentAir(player);
                    int newAir = currentAir.addDelta(delta, player);
                    player.setRemainingAir(newAir);
                });
            }
        }.runTaskTimer(plugin, 0, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    AirStatus status = playerAirs.get(player);
                    int air = status == null ? player.getRemainingAir() : status.getTicks();
                    if (air <= AirStatus.MIN_AIR) {
                        player.damage(1);
                    }
                });
            }
        }.runTaskTimer(plugin, 0, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    if (player.getInventory().getHelmet() == null
                            || player.getInventory().getHelmet().getEnchantmentLevel(Enchantment.WATER_WORKER) == 0) {
                        if (isUnderWater(player)) {
                            player.addPotionEffect(HASTE);
                            player.addPotionEffect(SPEED);
                        } else {
                            player.addPotionEffect(FATIGUE);
                            player.addPotionEffect(SLOWNESS);
                        }
                    }
                });
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private static int calculateDelta(Player player) {
        boolean isWaterlogged = false;
        if (player.getEyeLocation().getBlock().getBlockData() instanceof Waterlogged w) {
            isWaterlogged = w.isWaterlogged();
        }
        return isUnderWater(player)
                || (!player.getWorld().isClearWeather()
                && player.getEyeLocation().getBlock().getLightFromSky() != 0)
                || player.getEyeLocation().getBlock().getType() == Material.WATER
                || isWaterlogged ? AIR_REGAIN
                : -1;
    }

    private static boolean isUnderWater(Player player) {
        return player.getEyeLocation().getBlock().getType() == Material.WATER;
    }

    @NotNull
    public AirStatus getCurrentAir(Player player) {
        AirStatus air = this.playerAirs.get(player);
        if (air == null) {
            air = new AirStatus(player);
            playerAirs.put(player, air);
        }
        return air;
    }
}
