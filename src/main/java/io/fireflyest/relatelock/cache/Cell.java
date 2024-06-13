package io.fireflyest.relatelock.cache;

import javax.annotation.Nullable;

/**
 * 存储最小单元接口
 * @author Fireflyest
 * @since 1.0
 */
public interface Cell {
    
    @Nullable
    String get();

    void set();

}
