package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import io.fireflyest.relatelock.Print;

/**
 * 容器锁
 * @author Fireflyest
 * @since 1.0
 */
public class ContainerRelate extends Relate {

    protected ContainerRelate(@Nullable Block signBlock, @Nonnull Block attachBlock, int deep) {
        super(signBlock, attachBlock, deep);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getState() instanceof Container) {
            // 容器本身
            Print.RELATE_LOCK.debug("[{}]ContainerRelate.traceRelateBlocks() -> chest", deep);
            relateBlocks.add(attachBlock);
        }
    }
    
}
