package io.fireflyest.relatelock;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import io.fireflyest.relatelock.config.Config;
import io.fireflyest.relatelock.core.LocksmithImpl;
import io.fireflyest.relatelock.core.api.Locksmith;
import io.fireflyest.relatelock.listener.LockEventListener;

/**
 * 关联锁插件
 * 
 * @author Fireflyest
 * @since 1.0
 */
public final class RelateLock extends JavaPlugin {

    public RelateLock() {
        //
    }

    public static RelateLock getPlugin() {
        return getPlugin(RelateLock.class);
    }

    @Override
    public void onEnable() {
        Print.RELATE_LOCK.onDebug();

        final FileConfiguration configFile = this.getConfig();
        final Config config = new Config(configFile.getString("lockString"), 
                                         configFile.getString("shareString"));

        // 锁服务
        final LocksmithImpl locksmith = new LocksmithImpl();
        this.getServer().getServicesManager()
            .register(Locksmith.class, locksmith, this, ServicePriority.Normal);

        // 事件监听
        this.getServer().getPluginManager()
            .registerEvents(new LockEventListener(locksmith, config), this);
    }

    @Override
    public void onDisable() {
        // close data service
        
    }

}
