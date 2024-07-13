package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
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
        } else {
            Print.RELATE_LOCK.debug("OpenableRelate.traceRelateBlocks() -> openable");
            relateBlocks.add(attachBlock);
        }
    }
    
}
