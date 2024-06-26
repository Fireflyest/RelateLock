package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.Bed;
import io.fireflyest.relatelock.Print;

/**
 * 通用锁
 * @author Fireflyest
 * @since 1.0
 */
public class TileRelate extends Relate {

    protected TileRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getState() instanceof Container) { // 容器
            Print.RELATE_LOCK.debug("TileRelate.traceRelateBlocks() -> container");
            subRelate.add(new ContainerRelate(null, attachBlock));                
        } else if (attachBlock.getState().getBlockData() instanceof Bed) { // 床
            Print.RELATE_LOCK.debug("TileRelate.traceRelateBlocks() -> bed");
            subRelate.add(new BedRelate(null, attachBlock));
        } else { // 其他
            Print.RELATE_LOCK.debug("TileRelate.traceRelateBlocks() -> other");
            relateBlocks.add(attachBlock);
        }
    }

}
