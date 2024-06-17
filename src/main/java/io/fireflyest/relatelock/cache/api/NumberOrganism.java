package io.fireflyest.relatelock.cache.api;

import javax.annotation.Nonnull;

/**
 * 数字类型缓存
 * 
 * @author Fireflyest
 * @since 
 */
public interface NumberOrganism extends Organism<Number> {
    
    /**
     * 值增加
     * @param key 键
     * @param num 增加的值
     */
    void incrBy(@Nonnull String key, Number num);

    /**
     * 值增加1
     * @param key 键
     */
    void incr(@Nonnull String key);
    
    /**
     * 值减少
     * @param key 键
     * @param num 减少的值
     */
    void decrBy(@Nonnull String key, Number num);

    /**
     * 值减少1
     * @param key 键
     */
    void decr(@Nonnull String key);

}
