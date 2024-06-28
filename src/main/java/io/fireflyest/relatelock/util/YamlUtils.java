package io.fireflyest.relatelock.util;

import java.io.File;
import javax.annotation.Nonnull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 配置文件工具类
 * @author Fireflyest
 * @since 1.0
 */
public final class YamlUtils {
    
    public static final String DATA_PATH = "root";

    // 缓存解析
    private static final YamlConfiguration yaml = new YamlConfiguration();

    private YamlUtils() {
        //
    }

    /**
     * 序列化
     * @param configurationSerializable 可序列化对象
     * @return 文本数据
     */
    public static String serialize(ConfigurationSerializable configurationSerializable) {
        yaml.set(DATA_PATH, configurationSerializable);
        return yaml.saveToString();
    }

    /**
     * 反序列化
     * @param <T> 可序列化泛型
     * @param data 文本数据
     * @param clazz 可序列化对象的类
     * @return 可序列化对象
     */
    public static <T extends ConfigurationSerializable> T deserialize(String data, Class<T> clazz) {
        try {
            yaml.loadFromString(data);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return yaml.getSerializable(DATA_PATH, clazz);
    }

    /**
     * 序列化物品
     * @param itemStack 物品
     * @return 文本数据
     */
    public static String serializeItemStack(ItemStack itemStack) {
        return serialize(itemStack);
    }

    /**
     * 反序列化物品
     * @param stackData 物品数据
     * @return 物品
     */
    public static ItemStack deserializeItemStack(String stackData) {
        return deserialize(stackData, ItemStack.class);
    }

    public static FileConfiguration getConfig(@Nonnull JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        return plugin.getConfig();
    }

    /**
     * 更新配置的值
     * @param plugin 插件
     * @param key 键
     * @param obj 值
     */
    public static void setConfigData(@Nonnull JavaPlugin plugin, @Nonnull String key, Object obj) {
        plugin.getConfig().set(key, obj);
        plugin.saveConfig();
    }

    /**
     * 加载配置文件
     * @param plugin 插件
     * @param child 子路径
     * @return 配置文件
     */
    public static FileConfiguration loadYaml(@Nonnull JavaPlugin plugin, @Nonnull String child) {
        final File file = new File(plugin.getDataFolder(), child);
        if (!file.exists()) {
            plugin.saveResource(child, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

}
