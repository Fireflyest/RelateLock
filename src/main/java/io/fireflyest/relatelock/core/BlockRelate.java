package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Door;
import io.fireflyest.relatelock.Print;
import io.fireflyest.relatelock.util.BlockUtils;

/**
 * 非直接贴附锁
 * @author Fireflyest
 * @since 1.0
 */
public class BlockRelate extends Relate {

    protected BlockRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public void traceRelateBlocks() {
        // 不可附着方块
        if (attachBlock.isEmpty() || attachBlock.isLiquid() || attachBlock.isPassable()) {
            return;
        }

        boolean lockable = false;

        // 获取上下方的方块
        lockable |= this.tryLock(attachBlock.getRelative(BlockFace.DOWN));
        lockable |= this.tryLock(attachBlock.getRelative(BlockFace.UP));

        // 两侧
        if (signBlock != null && signBlock.getBlockData() instanceof Directional directional) {
            // 获取左侧的方块
            final BlockFace leftFace = BlockUtils.leftFace(directional.getFacing());
            lockable |= this.tryLock(attachBlock.getRelative(leftFace));

            // 获取右侧的方块
            final BlockFace rightFace = BlockUtils.rightFace(directional.getFacing());
            lockable |= this.tryLock(attachBlock.getRelative(rightFace));
        }
        
        // 被贴方块
        if (lockable) {
            relateBlocks.add(attachBlock);
        }
    }

    /**
     * 判断一个方块是否可锁，若可锁就添加关联
     * @param block 方块
     * @return 是否可锁
     */
    private boolean tryLock(@Nonnull Block block) {
        boolean lockable = true;
        if (block.getBlockData() instanceof Chest) { // 箱子
            Print.RELATE_LOCK.debug("BlockRelate.tryLock() -> chest");
            subRelate.add(new ChestRelate(null, block));
        } else if (block.getBlockData() instanceof Door) { //门，可能是多个上下分方块
            Print.RELATE_LOCK.debug("BlockRelate.tryLock() -> door");
            subRelate.add(new DoorRelate(null, block));
        } else if (block.getBlockData() instanceof Bisected) { // 上下分方块
            Print.RELATE_LOCK.debug("BlockRelate.tryLock() -> bisected");
            subRelate.add(new BisectedRelate(null, block));
        } else if (block.getState() instanceof TileState) { // 其他可更新方块
            Print.RELATE_LOCK.debug("BlockRelate.tryLock() -> tile");
            subRelate.add(new TileRelate(null, block));
        } else {
            lockable = false;
        }
        return lockable;
    }

    
}
