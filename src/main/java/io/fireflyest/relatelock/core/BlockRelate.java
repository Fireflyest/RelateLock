package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Directional;

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

        // 被贴方块
        relateBlocks.add(attachBlock);

        // 获取下方的方块
        final Block down = attachBlock.getRelative(BlockFace.DOWN);
        if (down.getState() instanceof TileState) {
            subRelate.add(new TileRelate(null, down));
        }
        // 获取上方的方块
        final Block up = attachBlock.getRelative(BlockFace.UP);
        if (up.getState() instanceof TileState) {
            subRelate.add(new TileRelate(null, up));
        }

        // 两侧
        if (signBlock != null && signBlock.getState() instanceof Directional directional) {
            // 获取左侧的方块
            final BlockFace leftFace = BlockUtils.leftFace(directional.getFacing());
            final Block left = attachBlock.getRelative(leftFace);
            if (left.getState() instanceof TileState) {
                subRelate.add(new TileRelate(null, left));
            }
            // 获取右侧的方块
            final BlockFace rightFace = BlockUtils.rightFace(directional.getFacing());
            final Block right = attachBlock.getRelative(rightFace);
            if (right.getState() instanceof TileState) {
                subRelate.add(new TileRelate(null, right));
            }
        }
        
    }

    
}
