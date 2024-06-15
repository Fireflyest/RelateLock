package io.fireflyest.relatelock.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import io.fireflyest.relatelock.cache.api.Cell;

/**
 * 缓存数据存储实现类
 * @author Fireflyest
 * @since 1.0
 */
public class CacheCell implements Cell<String> {

    private final Set<String> valueSet;
    private final Instant born;
    private Instant deadline;

    /**
     * 缓存
     * @param expire 失效时间，单位毫秒
     * @param value 值
     */
    public CacheCell(long expire, String value) {
        this(expire, new HashSet<>(Set.of(value)));
    }

    /**
     * 缓存
     * @param expire 失效时间，单位毫秒
     * @param valueSet 值集
     */
    public CacheCell(long expire, Set<String> valueSet) {
        this.valueSet = valueSet;
        this.born = Instant.now();
        this.deadline = expire == -1 ? null : Instant.now().plusMillis(expire);
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
        return deadline == null || Instant.now().isBefore(deadline) ? valueSet : null;
    }

    @Override
    public long ttl() {
        if (deadline == null) return -1;
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

    
    
}
