package io.fireflyest.relatelock.cache.api;

import javax.annotation.Nonnull;

/**
 * 文本类型缓存
 * 
 * @author Fireflyest
 * @since 
 */
public interface StringOrganism extends Organism<String> {
    
    /**
     * 拼接字符串
     * @param key 键
     * @param substring 拼接值
     */
    void append(@Nonnull String key, String substring);

}
