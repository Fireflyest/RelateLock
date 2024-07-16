package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import io.fireflyest.relatelock.Print;

/**
 * 上下二分方块锁，例如门
 * @author Fireflyest
 * @since 1.0
 */
public class BisectedRelate extends Relate {

    protected BisectedRelate(@Nullable Block signBlock, @Nonnull Block attachBlock, int deep) {
        super(signBlock, attachBlock, deep);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getBlockData() instanceof Bisected bisected) {
            // 自己
            Print.RELATE_LOCK.debug("[{}]BisectedRelate.traceRelateBlocks() -> half", deep);
            relateBlocks.add(attachBlock);

            // 另一部分，和基座
            final Block anotherPart;
            final Block baseBlock;
            switch (bisected.getHalf()) {
                case TOP -> {
                    anotherPart = attachBlock.getRelative(BlockFace.DOWN);
                    baseBlock = attachBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
                }
                case BOTTOM -> {
                    anotherPart = attachBlock.getRelative(BlockFace.UP);
                    baseBlock = attachBlock.getRelative(BlockFace.DOWN);
                }
                default -> {
                    anotherPart = null;
                    baseBlock = null;
                }
            }

            // 另一半
            if (anotherPart != null && anotherPart.getBlockData() instanceof Bisected) {
                Print.RELATE_LOCK.debug("[{}]BisectedRelate.traceRelateBlocks() -> half", deep);
                relateBlocks.add(anotherPart);
            }
            // 保护基座防止门被简介破坏
            if (baseBlock != null) {
                relateBlocks.add(baseBlock);
                Print.RELATE_LOCK.debug("[{}]BisectedRelate.traceRelateBlocks() -> base", deep);
            }
        }
    }

}
