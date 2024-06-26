package io.fireflyest.relatelock;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import io.fireflyest.relatelock.config.Config;
import io.fireflyest.relatelock.core.LocksmithImpl;
import io.fireflyest.relatelock.core.api.Locksmith;
import io.fireflyest.relatelock.listener.LockEventListener;
import io.fireflyest.relatelock.util.YamlUtils;

/**
 * 关联锁插件
 * 
 * @author Fireflyest
 * @since 1.0
 */
public final class RelateLock extends JavaPlugin {

    private LocksmithImpl locksmith;

    public RelateLock() {
        //
    }

    public static RelateLock getPlugin() {
        return getPlugin(RelateLock.class);
    }

    @Override
    public void onEnable() {
        Print.RELATE_LOCK.onDebug();

        Print.RELATE_LOCK.info("Loading config.");
        final FileConfiguration configFile = YamlUtils.getConfig(this);
        final Config config = new Config(configFile.getString("LockString"), 
                                         configFile.getString("ShareString"));

        // 锁服务
        this.locksmith = new LocksmithImpl();
        Print.RELATE_LOCK.info("Loading lock's data from cache file.");
        this.locksmith.load(this);
        this.getServer().getServicesManager()
            .register(Locksmith.class, locksmith, this, ServicePriority.Normal);

        // 事件监听
        this.getServer().getPluginManager()
            .registerEvents(new LockEventListener(locksmith, config), this);
    }

    @Override
    public void onDisable() {
        Print.RELATE_LOCK.info("Saving lock's data to cache file.");
        this.locksmith.save(this);
        // close data service
        
    }

}
