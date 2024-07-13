package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
import io.fireflyest.relatelock.Print;

/**
 * 可开关方块关联
 * 
 * @author Fireflyest
 * @since 1.0
 */
public class OpenableRelate extends Relate {

    protected OpenableRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getBlockData() instanceof Door) {
            Print.RELATE_LOCK.debug("OpenableRelate.traceRelateBlocks() -> door");
            subRelate.add(new DoorRelate(null, attachBlock));
        } else if (attachBlock.getBlockData() instanceof TrapDoor) {
            Print.RELATE_LOCK.debug("OpenableRelate.traceRelateBlocks() -> trap door");
            subRelate.add(new TrapDoorRelate(null, attachBlock));
        } else {
            Print.RELATE_LOCK.debug("OpenableRelate.traceRelateBlocks() -> openable");
            relateBlocks.add(attachBlock);
        }
    }
    
}
