package io.fireflyest.relatelock.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import io.fireflyest.relatelock.cache.api.Cell;

/**
 * 缓存数据存储抽象类
 * 
 * @author Fireflyest
 * @since 1.0
 */
public abstract class AbstractCell<T> implements Cell<T> {

    protected final Instant born;
    protected Instant deadline;

    protected final Set<T> valueSet;

    /**
     * 用于读取数据的构造函数
     * 
     * @param born 起始时间
     * @param deadline 失效时间
     * @param valueSet 数据集
     */
    protected AbstractCell(Instant born, Instant deadline, Set<T> valueSet) {
        this.born = born;
        this.deadline = deadline;
        this.valueSet = valueSet;
    }

    /**
     * 单值构造函数
     * @param expire 失效时间，单位毫秒，值小于等于0表示无限制
     * @param value 值
     */
    protected AbstractCell(long expire, T value) {
        this(expire, new HashSet<>(Set.of(value)));
    }

    /**
     * 数据集构造函数
     * @param expire 失效时间，单位毫秒，值小于等于0表示无限制
     * @param valueSet 值集
     */
    protected AbstractCell(long expire, Set<T> valueSet) {
        this(Instant.now(), expire <= 0 ? null : Instant.now().plusMillis(expire), valueSet);
    }

    @Override
    @Nullable
    public T get() {
        // 无限期或者在期限内返回数据
        if (!valueSet.isEmpty() && (deadline == null || Instant.now().isBefore(deadline))) {
            return valueSet.iterator().next();
        }
        return null;
    }

    @Override
    @Nullable
    public Set<T> getAll() {
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
        // 期限到了返回0
        if (deadline == null) {
            return -1;
        }
        final long ms = Duration.between(Instant.now(), deadline).toMillis();
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
    
    @Override
    public Instant born() {
        return born;
    }

    @Override
    @Nullable
    public Instant deadline() {
        return deadline;
    }

}
