package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import io.fireflyest.relatelock.Print;
import io.fireflyest.relatelock.util.BlockUtils;

/**
 * 门锁
 * @author Fireflyest
 * @since 1.0
 */
public class DoorRelate extends Relate {

    protected DoorRelate(@Nullable Block signBlock, @Nonnull Block attachBlock, int deep) {
        super(signBlock, attachBlock, deep);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getBlockData() instanceof Door) {
            // 自己
            Print.RELATE_LOCK.debug("[{}]DoorRelate.traceRelateBlocks() -> bisected", deep);
            subRelate.add(new BisectedRelate(null, attachBlock, this.deep + 1));
        }

        // 另一扇门
        final Block anotherDoor = BlockUtils.anotherDoor(attachBlock);
        if (anotherDoor != null && anotherDoor.getBlockData() instanceof Door) {
            Print.RELATE_LOCK.debug("[{}]DoorRelate.traceRelateBlocks() -> bisected", deep);
            subRelate.add(new BisectedRelate(null, anotherDoor, this.deep + 1));
        }
    }

}
