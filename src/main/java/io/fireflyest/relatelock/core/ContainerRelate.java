package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.Container;

/**
 * 容器锁
 * @author Fireflyest
 * @since 1.0
 */
public class ContainerRelate extends Relate {

    protected ContainerRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getState() instanceof Container) {
            // 容器本身
            relateBlocks.add(attachBlock);
        }
    }
    
}
