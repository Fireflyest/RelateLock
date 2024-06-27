package io.fireflyest.relatelock.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import io.fireflyest.relatelock.Print;

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

    /**
     * 更新配置的值
     * @param plugin 插件
     * @param key 键
     * @param value 值
     */
    public void setConfigData(@Nonnull JavaPlugin plugin, @Nonnull String key, Object value) {
        plugin.getConfig().set(key, value);
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

    public static void test() {
        File file = new File("E:\\Project\\Spigot\\RelateLock\\src\\main\\resources\\config.yml");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        for (Entry<String,Object> entrySet : configuration.getValues(true).entrySet()) {
            System.out.println(entrySet.getKey() + " = " + entrySet.getValue());
            if (entrySet.getValue().getClass().equals(ArrayList.class)) {
                List<String> strings = StringUtils.jsonToList(entrySet.getValue().toString());
                strings.forEach(System.out::println);
            }
        }
    }

}
