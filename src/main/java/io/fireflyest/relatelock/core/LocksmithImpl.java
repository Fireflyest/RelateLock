package io.fireflyest.relatelock.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.bukkit.Chunk;
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
    
    private final Map<Chunk, Set<Block>> lockedMap = new HashMap<>();

    public LocksmithImpl() {
        
    }

    boolean lock(@Nonnull final Block block, @Nonnull final Lock lock) {
        // 牌子的方向
        final Directional directional = ((Directional) block.getBlockData());

        final Block attachBlock = block.getRelative(directional.getFacing().getOppositeFace());
        Relate relate;

        // chest tile block

        // chest s b
        // tile container door bed other
        // block -

        if (attachBlock.getState() instanceof Chest chest) { // 箱子
            
        } else if (attachBlock.getState() instanceof TileState) { // 除了箱子外的可操作方块

        } else { // 上锁贴着方块附近的方块
            
        }
        return false;
    }

    boolean isLocked(@Nonnull final Block block) {
        final Set<Block> lockedSet = lockedMap.get(block.getChunk());
        return lockedSet != null && lockedSet.contains(block);
    }

    
    private Set<Block> tileRelate(@Nonnull Block block) {
        final Set<Block> blocks = new HashSet<>();
        var canLock = true;
        blocks.add(block);

        return canLock ? blocks : null;
    }

    private Set<Block> doorRelate(@Nonnull Block block) {
        final Set<Block> blocks = new HashSet<>();
        var canLock = true;
        blocks.add(block);

        return canLock ? blocks : null;
    }

    private Set<Block> containerRelate(@Nonnull Block block) {
        final Set<Block> blocks = new HashSet<>();
        var canLock = true;
        blocks.add(block);

        return canLock ? blocks : null;
    }

    private Set<Block> chestRelate(@Nonnull Block block) {
        // if (container.getInventory() instanceof DoubleChestInventory doubleChestInventory) {
                
        // } else {

        // }
        final Set<Block> blocks = new HashSet<>();
        var canLock = true;
        blocks.add(block);

        return canLock ? blocks : null;
    }

}
