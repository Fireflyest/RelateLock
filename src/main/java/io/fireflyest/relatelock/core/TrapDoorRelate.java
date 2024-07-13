package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrapDoor;
import io.fireflyest.relatelock.Print;
import io.fireflyest.relatelock.util.BlockUtils;

/**
 * 门锁
 * @author Fireflyest
 * @since 1.0
 */
public class TrapDoorRelate extends Relate {

    protected TrapDoorRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getBlockData() instanceof TrapDoor) {
            // 自己
            Print.RELATE_LOCK.debug("TrapDoorRelate.traceRelateBlocks() -> half");
            relateBlocks.add(attachBlock);
        }

        // 另一扇门
        final Block anotherDoor = BlockUtils.anotherTrapDoor(attachBlock);
        if (anotherDoor != null && anotherDoor.getBlockData() instanceof TrapDoor) {
            Print.RELATE_LOCK.debug("TrapDoorRelate.traceRelateBlocks() -> half");
            relateBlocks.add(anotherDoor);
        }
    }

}
