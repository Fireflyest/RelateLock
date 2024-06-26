package io.fireflyest.relatelock.core.api;

import javax.annotation.Nonnull;
import org.bukkit.Location;
import org.bukkit.block.Block;
import io.fireflyest.relatelock.bean.Lock;

/**
 * 锁匠，锁的控制类
 * 
 * @author Fireflyest
 * @since 1.0
 */
public interface Locksmith {
    
    /**
     * 尝试给方块上锁，上锁成功返回true
     * 
     * @param signBlock 牌子方块
     * @param lock 锁
     * @return 是否可以关联锁
     */
    boolean lock(@Nonnull Block signBlock, @Nonnull Lock lock);

    /**
     * 解锁方块
     * 
     * @param signLocation 牌子
     */
    void unlock(@Nonnull Location signLocation);

    /**
     * 使用
     * 
     * @param location 方块位置
     * @param uid 玩家uuid
     * @param name 玩家名称
     * @return 是否可用
     */
    boolean use(@Nonnull Location location, @Nonnull String uid, @Nonnull String name);

    /**
     * 破坏方块
     * 
     * @param location 方块位置
     * @param uid 玩家uuid
     * @param name 玩家名称
     * @return 是否可破坏
     */
    boolean destroy(@Nonnull Location location, @Nonnull String uid, @Nonnull String name);

}
