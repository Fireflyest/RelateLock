package io.fireflyest.relatelock.cache.api;

import java.util.Set;
import javax.annotation.Nullable;

/**
 * 存储最小单元接口
 * @author Fireflyest
 * @since 1.0
 */
public interface Cell<T> {
    
    /**
     * 获取数据
     * @return 数据
     */
    @Nullable
    T get();

    /**
     * 获取数据集合
     * @return 集合
     */
    @Nullable
    Set<T> getAll();
    
    /**
     * 数据剩余保留时间，如果没有限制，返回-1；如果到期，返回0
     * @return 剩余毫秒
     */
    public long ttl();

    /**
     * 设置数据保留时间，过期数据只能获取为null
     * @param ms 保留毫秒数
     */
    public void expire(long ms);

    /**
     * 获取数据的存在时间
     * @return 存在毫秒数
     */
    public long age();

    /**
     * 设置为无限期数据。
     */
    public void persist();

}
