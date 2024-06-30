package io.fireflyest.relatelock.core.api;

import org.bukkit.Location;

/**
 * 锁操作的结果
 * 
 * @author Fireflyest
 * @since 1.0
 */
public final class Result {
    
    /**
     * 是否通过
     */
    private final boolean access;

    /**
     * 操作所需权限等级
     */
    private final int permission;
    // private final 操作;


    public Result(boolean access, int permission) {
        this.access = access;
        this.permission = permission;
    }

    // private final Location location;

    
}
