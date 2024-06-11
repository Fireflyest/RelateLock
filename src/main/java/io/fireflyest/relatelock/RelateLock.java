package io.fireflyest.relatelock;

import org.bukkit.plugin.java.JavaPlugin;

import io.fireflyest.relatelock.listener.LockEventListener;

public final class RelateLock extends JavaPlugin {
    
    public static RelateLock getPlugin() {
        return getPlugin(RelateLock.class);
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new LockEventListener(), this);
    }

    @Override
    public void onDisable() {
        // close data service
        
    }
}
