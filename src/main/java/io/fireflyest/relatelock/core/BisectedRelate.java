package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;

/**
 * 上下二分方块锁，例如门
 * @author Fireflyest
 * @since 1.0
 */
public class BisectedRelate extends Relate {

    protected BisectedRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock instanceof Bisected bisected) {
            // 自己
            relateBlocks.add(attachBlock);
            // 另一部分，和基座
            final BlockFace face;
            final Block baseBlock;
            switch (bisected.getHalf()) {
                case TOP -> {
                    face = BlockFace.DOWN;
                    baseBlock = attachBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
                }
                case BOTTOM -> {
                    face = BlockFace.UP;
                    baseBlock = attachBlock.getRelative(BlockFace.DOWN);
                }
                default -> {
                    face = null;
                    baseBlock = null;
                }
            }
            if (face != null) {
                final Block otherPart = attachBlock.getRelative(face);
                // 某些情况下门只有一半
                if (otherPart.getBlockData() instanceof Bisected) {
                    relateBlocks.add(otherPart);
                }
            }
            // 保护基座防止门被简介破坏
            if (baseBlock != null) {
                relateBlocks.add(baseBlock);
            }
        }
    }

}
