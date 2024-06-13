package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.block.Container;

/**
 * 锁匠，锁的控制类
 * @author Fireflyest
 * @since 1.0
 */
public interface Locksmith {
    
    boolean canLock(@Nonnull Block block);

    boolean lock(@Nonnull Block block);

    boolean lockContainer(@Nonnull Container container);

}
