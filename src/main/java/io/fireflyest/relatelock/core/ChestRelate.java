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

    protected ChestRelate(@Nullable Block signBlock, @Nonnull Block attachBlock, int deep) {
        super(signBlock, attachBlock, deep);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getBlockData() instanceof Chest) {
            // 自己
            Print.RELATE_LOCK.debug("ChestRelate.traceRelateBlocks() -> chest");
            subRelate.add(new ContainerRelate(null, attachBlock, this.deep + 1));
        }

        // 另一半
        final Block anotherChest = BlockUtils.anotherChest(attachBlock);
        if (anotherChest != null && anotherChest.getBlockData() instanceof Chest) {
            Print.RELATE_LOCK.debug("ChestRelate.traceRelateBlocks() -> chest");
            subRelate.add(new ContainerRelate(null, anotherChest, this.deep + 1));
        }
    }
    
}
