package io.fireflyest.relatelock.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import io.fireflyest.relatelock.cache.api.Cell;

/**
 * 缓存数据存储实现类
 * 
 * @author Fireflyest
 * @since 1.0
 */
public class CacheCell implements Cell<String> {

    private final Instant born;
    private Instant deadline;
    private final Set<String> valueSet;

    /**
     * 用于读取数据的构造函数
     * 
     * @param born 起始时间
     * @param deadline 失效时间
     * @param valueSet 数据集
     */
    public CacheCell(Instant born, Instant deadline, Set<String> valueSet) {
        this.born = born;
        this.deadline = deadline;
        this.valueSet = valueSet;
    }

    /**
     * 单值构造函数
     * @param expire 失效时间，单位毫秒，值小于等于0表示无限制
     * @param value 值
     */
    public CacheCell(long expire, String value) {
        this(expire, new HashSet<>(Set.of(value)));
    }

    /**
     * 数据集构造函数
     * @param expire 失效时间，单位毫秒，值小于等于0表示无限制
     * @param valueSet 值集
     */
    public CacheCell(long expire, Set<String> valueSet) {
        this(Instant.now(), expire <= 0 ? null : Instant.now().plusMillis(expire), valueSet);
    }

    @Override
    @Nullable
    public String get() {
        // 无限期或者在期限内返回数据
        if (deadline == null || Instant.now().isBefore(deadline)) {
            return valueSet.size() == 1 ? valueSet.toArray(new String[0])[0] : valueSet.toString();
        }
        return null;
    }

    @Override
    @Nullable
    public Set<String> getAll() {
        // 无限期或者在期限内返回数据
        if (deadline == null || Instant.now().isBefore(deadline)) {
            return valueSet;
        }
        // 数据失效，清空集合并返回空
        valueSet.clear();
        return null;
    }

    @Override
    public long ttl() {
        if (deadline == null) {
            return -1;
        }
        long ms = Duration.between(Instant.now(), deadline).toMillis();
        return Math.max(ms, 0);
    }

    @Override
    public void expire(long ms) {
        deadline = Instant.now().plusMillis(ms);
    }

    @Override
    public long age() {
        return Duration.between(born, Instant.now()).toMillis();
    }

    @Override
    public void persist() {
        deadline = null;
    }

    /**
     * 获取起始时间
     * @return 起始时间
     */
    public Instant getBorn() {
        return born;
    }

    /**
     * 获取失效时间
     * @return 失效时间
     */
    @Nullable
    public Instant getDeadline() {
        return deadline;
    }
    
}
