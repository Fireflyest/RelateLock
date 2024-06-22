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
            // 另一部分
            final BlockFace face = switch (bisected.getHalf()) {
                case TOP -> BlockFace.DOWN;
                case BOTTOM -> BlockFace.UP;
                default -> null;
            };
            if (face != null) {
                final Block otherPart = attachBlock.getRelative(face);
                // 某些情况下门只有一半
                if (otherPart.getBlockData() instanceof Bisected) {
                    relateBlocks.add(otherPart);
                }
            }
        }
    }

}
