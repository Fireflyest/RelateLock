package io.fireflyest.relatelock.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
    private final String value;
    private final boolean isSet;
    private final Instant born;
    private Instant deadline;

    /**
     * 缓存
     * @param expire 失效时间，单位毫秒
     * @param values 值
     */
    public CacheCell(long expire, String... values) {
        this.isSet = values.length > 1;
        this.value = values[0];
        this.valueSet = new HashSet<>();
        this.born = Instant.now();
        this.deadline = expire == -1 ? null : Instant.now().plusMillis(expire);

        Collections.addAll(valueSet, values);
    }

    @Override
    @Nullable
    public String get() {
        // 无限期或者在期限内返回数据
        boolean valid = deadline == null || Instant.now().isBefore(deadline);
        if (valid) {
            return isSet ? valueSet.toString() : value;
        }
        return null;
    }

    @Override
    @Nullable
    public Set<String> getSet() {
        return deadline == null || Instant.now().isBefore(deadline) ? valueSet : null;
    }

    @Override
    public long ttl() {
        if (deadline == null) return -1;
        long second = Duration.between(Instant.now(), deadline).toMillis();
        return Math.max(second, 0);
    }

    @Override
    public void expire(long ms) {
        deadline = Instant.now().plusMillis(ms);
    }

    @Override
    public long age() {
        return Duration.between(born, Instant.now()).toSeconds();
    }

    @Override
    public void persist() {
        deadline = null;
    }

    
    
}
