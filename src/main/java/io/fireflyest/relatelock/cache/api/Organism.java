package io.fireflyest.relatelock.cache.api;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.plugin.Plugin;

/**
 * 存储字典
 * @author Fireflyest
 * @since 1.0
 */
public interface Organism<K, V> {

    /**
     * 删除数据
     * @param key 键
     */
    void del(@Nonnull K key);

    /**
     * 设置数据期限
     * @param key 键
     * @param ms 失效时间，单位毫秒
     */
    void expire(@Nonnull K key, int ms);

    /**
     * 判断某个键是否存在
     * @param key 键
     * @return 是否存在
     */
    boolean exist(@Nonnull K key);

    /**
     * 设置显示数据为永久
     * @param key 键
     */
    void persist(@Nonnull K key);

    /**
     * 数据剩余保留时间
     * @param key 键
     * @return 剩余时间，单位毫秒
     */
    long ttl(@Nonnull K key);

    /**
     * 设置无期限数据集合
     * @param key 键
     * @param valueSet 值集
     */
    void set(@Nonnull K key, Set<V> valueSet);

    /**
     * 设置无期限数据
     * @param key 键
     * @param value 值
     */
    void set(@Nonnull K key, V value);

    /**
     * 设置限期数据
     * @param key 键
     * @param ms 失效时间，单位毫秒
     * @param valueSet 值集
     */
    void setex(@Nonnull K key, int ms, Set<V> valueSet);

    /**
     * 设置限期数据
     * @param key 键
     * @param ms 失效时间，单位毫秒
     * @param value 值
     */
    void setex(@Nonnull K key, int ms, V value);

    /**
     * 获取数据
     * @param key 键
     * @return 值
     */
    @Nullable
    V get(@Nonnull K key);

    /**
     * 获取数据存在时间，数据不存在则返回0
     * @param key 键
     * @return 存在时间，单位毫秒
     */
    long age(@Nonnull K key);

    /**
     * 添加一个数据到集
     * @param key 键
     * @param value 值
     */
    void sadd(@Nonnull K key, V value);

    /**
     * 获取某集所有数据
     * @param key 键
     * @return 数据集
     */
    @Nullable
    Set<V> smembers(@Nonnull K key);

    /**
     * 集合是否存在某个数据
     * 
     * @param key 键
     * @param value 值
     * @return 是否存在
     */
    boolean sexist(@Nonnull K key, V value);

    /**
     * 删除集里的元素
     * @param key 键
     * @param value 值
     */
    void srem(@Nonnull K key, V value);

    /**
     * 随机出栈一个值
     * @param key 键
     * @return 值
     */
    @Nullable
    V spop(@Nonnull K key);

    /**
     * 获取集合里面的元素数量，数据不存在返回0
     * @param key 键
     * @return 数量
     */
    int scard(@Nonnull K key);

    /**
     * 清理过期的键，以便内存释放
     */
    void release();

    /**
     * 获取键的集合
     * @return 键的集合
     */
    Set<K> keySet();

    /**
     * 反序列化键
     * @param keyStr 文本
     * @return 键
     */
    K deserializeKey(@Nonnull String keyStr);

    /**
     * 反序列化值
     * @param valueStr 文本
     * @return 值
     */
    V deserializeValue(@Nonnull String valueStr);

    /**
     * 序列化键
     * @param key 键
     * @return 文本
     */
    String serializeKey(@Nonnull K key);

    /**
     * 序列化值
     * @param value 值
     * @return 文本
     */
    String serializeValue(@Nonnull V value);

    /**
     * 从插件的目录里加载缓存
     * @param plugin 插件
     * @param entryName 存储分支
     * @param reset 加载前重置数据
     */
    void load(@Nonnull Plugin plugin, @Nonnull String entryName, boolean reset);

    /**
     * 从插件的目录里加载缓存
     * @param plugin 插件
     * @param entryName 存储分支
     */
    void load(@Nonnull Plugin plugin, @Nonnull String entryName);

    /**
     * 从插件的目录里加载缓存
     * @param plugin 插件
     */
    void load(@Nonnull Plugin plugin);

    /**
     * 保存缓存到插件的目录下
     * @param plugin 插件
     * @param entryName 存储分支
     * @param reset 保存后重置数据
     */
    void save(@Nonnull Plugin plugin, @Nonnull String entryName, boolean reset);

    /**
     * 保存缓存到插件的目录下
     * @param plugin 插件
     * @param entryName 存储分支
     */
    void save(@Nonnull Plugin plugin, @Nonnull String entryName);

    /**
     * 保存缓存到插件的目录下
     * @param plugin 插件
     */
    void save(@Nonnull Plugin plugin);

}
