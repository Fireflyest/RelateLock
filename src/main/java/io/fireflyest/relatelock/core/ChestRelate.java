package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Chest;
import io.fireflyest.relatelock.Print;
import io.fireflyest.relatelock.util.BlockUtils;

/**
 * 箱子锁
 * @author Fireflyest
 * @since 1.0
 */
public class ChestRelate extends Relate {

    protected ChestRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getState().getBlockData() instanceof Chest chest) {
            // 自己
            Print.RELATE_LOCK.debug("ChestRelate.traceRelateBlocks() -> chest");
            subRelate.add(new ContainerRelate(null, attachBlock));

            // 另一半
            final Block anotherChest = switch (chest.getType()) {
                case LEFT -> attachBlock.getRelative(BlockUtils.rightFace(chest.getFacing()));
                case RIGHT -> attachBlock.getRelative(BlockUtils.leftFace(chest.getFacing()));
                case SINGLE -> null;
                default -> null;
            };
            if (anotherChest != null && anotherChest.getState().getBlockData() instanceof Chest) {
                Print.RELATE_LOCK.debug("ChestRelate.traceRelateBlocks() -> chest");
                subRelate.add(new ContainerRelate(null, anotherChest));
            }
        }
    }
    
}
