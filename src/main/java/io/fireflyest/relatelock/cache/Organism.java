package io.fireflyest.relatelock.cache;

import java.util.Set;

import javax.annotation.Nonnull;

/**
 * 存储字典
 * @author Fireflyest
 * @since 1.0
 */
public interface Organism<T> {

    /**
     * 删除数据
     * @param key 键
     */
    void del(@Nonnull String key);

    /**
     * 设置数据期限
     * @param key 键
     * @param ms 失效时间，单位毫秒
     */
    void expire(@Nonnull String key, int ms);

    /**
     * 判断某个键是否存在
     * @param key 键
     * @return 是否存在
     */
    boolean exist(@Nonnull String key);

    /**
     * 设置显示数据为永久
     * @param key 键
     */
    void persist(@Nonnull String key);

    /**
     * 数据剩余保留时间
     * @param key 键
     * @return 剩余时间，单位毫秒
     */
    long ttl(@Nonnull String key);

    /**
     * 设置无期限数据
     * @param key 键
     * @param value 值
     */
    void set(@Nonnull String key, T value);

    /**
     * 设置无期限数据集合
     * @param key 键
     * @param valueSet 值集
     */
    void set(@Nonnull String key, Set<T> valueSet);

    /**
     * 设置限期数据
     * @param key 键
     * @param ms 失效时间，单位毫秒
     * @param value 值
     */
    void setex(@Nonnull String key, int ms, T value);

    /**
     * 设置限期数据
     * @param key 键
     * @param ms 失效时间，单位毫秒
     * @param valueSet 值集
     */
    void setex(@Nonnull String key, int ms, Set<T> valueSet);

    /**
     * 获取数据
     * @param key 键
     * @return 值
     */
    T get(@Nonnull String key);

    /**
     * 获取数据存在时间
     * @param key 键
     * @return 存在时间，单位毫秒
     */
    long age(@Nonnull String key);

    /**
     * 添加一个数据到集
     * @param key 键
     * @param value 值
     */
    void sadd(@Nonnull String key, T value);

    /**
     * 获取某集所有数据
     * @param key 键
     * @return 数据集
     */
    Set<T> smembers(@Nonnull String key);

    /**
     * 删除集里的元素
     * @param key 键
     * @param value 值
     */
    void srem(@Nonnull String key, T value);

    /**
     * 随机出栈一个值
     * @param key 键
     * @return 值
     */
    T spop(@Nonnull String key);

    /**
     * 获取集合里面的元素数量
     * @param key 键
     * @return 数量
     */
    int scard(@Nonnull String key);

}
