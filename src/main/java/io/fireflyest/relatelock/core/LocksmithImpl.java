package io.fireflyest.relatelock.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Bed;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Door;
import org.bukkit.inventory.DoubleChestInventory;

import io.fireflyest.relatelock.bean.Lock;

/**
 * 锁匠实现类
 * @author Fireflyest
 * @since 1.0
 */
public final class LocksmithImpl {
    
    private final Map<Chunk, Set<Location>> lockedMap = new HashMap<>();

    public LocksmithImpl() {
        
    }

    boolean lock(@Nonnull final Block signBlock, @Nonnull final Lock lock) {
        // 获取被贴方块
        final Directional directional = ((Directional) signBlock.getBlockData());
        final Block attachBlock = signBlock.getRelative(directional.getFacing().getOppositeFace());
        // 获取关联
        final Relate relate;
        if (attachBlock.getState() instanceof Chest) { // 箱子
            relate = new ChestRelate(signBlock, attachBlock);
        } else if (attachBlock.getState() instanceof TileState) { // 除了箱子外的可操作方块
            relate = new TileRelate(signBlock, attachBlock);
        } else { // 上锁贴着方块附近的方块
            relate = new BlockRelate(signBlock, attachBlock);
        }
        // 判断是否全可锁
        for (Block relateBlock : relate.getRelateBlocks()) {
            if (this.isLocked(relateBlock.getLocation())) {
                return false;
            }
        }
        // 上锁
        // block -> sign
        // sign -> owner uuid manager[uuid] share[uuid] outset type password log[uuid-time] 

        // 缓存思想 uuid - loc
        for (Block relateBlock : relate.getRelateBlocks()) {
            // String locKey = 
        }
        return true;
    }

    boolean isLocked(@Nonnull final Location location) {
        final Set<Location> lockedSet = lockedMap.get(location.getChunk());
        return lockedSet != null && lockedSet.contains(location);
    }

}
