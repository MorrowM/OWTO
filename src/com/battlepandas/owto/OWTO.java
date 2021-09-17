package com.battlepandas.owto;

import org.bukkit.plugin.java.JavaPlugin;

public class OWTO extends JavaPlugin {
    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(new WaterBreathing(this), this);
    }
}
