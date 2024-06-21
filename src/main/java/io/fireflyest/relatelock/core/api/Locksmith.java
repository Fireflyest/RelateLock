package io.fireflyest.relatelock.core.api;

import java.util.Set;
import javax.annotation.Nonnull;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;

/**
 * 锁匠，锁的控制类
 * @author Fireflyest
 * @since 1.0
 */
public interface Locksmith {
    
    /**
     * 判断牌子所影响的所有方块是否可以上锁，包括牌子本身，
     * 单个容器上锁应该包括容器下方方块，
     * 大型箱子应该包括左右两部分及其下方方块，
     * 门应该包括横向三个门及其下方方块。
     * 
     * @param sign 方块
     * @return 是否可以关联锁
     */
    boolean canLock(@Nonnull Sign sign);

    boolean lock(@Nonnull Sign sign);

    Set<Block> relateBlocks(@Nonnull Sign sign);

    boolean lockContainer(@Nonnull Container container);

}
