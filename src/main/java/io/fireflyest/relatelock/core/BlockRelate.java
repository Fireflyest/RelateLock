package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Chest;
import io.fireflyest.relatelock.Print;
import io.fireflyest.relatelock.util.BlockUtils;

/**
 * 非直接贴附锁
 * @author Fireflyest
 * @since 1.0
 */
public class BlockRelate extends Relate {

    protected BlockRelate(@Nullable Block signBlock, @Nonnull Block attachBlock, int deep) {
        super(signBlock, attachBlock, deep);
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
            Print.RELATE_LOCK.debug("[{}]BlockRelate.tryLock() -> chest", deep);
            subRelate.add(new ChestRelate(null, block, this.deep + 1));
        } else if (block.getBlockData() instanceof Openable) { // 可开关方块
            Print.RELATE_LOCK.debug("[{}]BlockRelate.tryLock() -> openable", deep);
            subRelate.add(new OpenableRelate(null, block, this.deep + 1));
        } else if (block.getState() instanceof TileState) { // 实体方块
            Print.RELATE_LOCK.debug("[{}]BlockRelate.tryLock() -> tile", deep);
            subRelate.add(new TileRelate(null, block, this.deep + 1));
        } else {
            lockable = false;
        }
        return lockable;
    }

    
}
