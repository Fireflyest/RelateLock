package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;

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
        if (attachBlock.getState() instanceof TileState tileState) {
            if (tileState instanceof Container) { // 容器
                subRelate.add(new ContainerRelate(null, attachBlock));
            } else if (attachBlock.getBlockData() instanceof Door) { // 门，可能是多个上下分方块
                subRelate.add(new DoorRelate(null, attachBlock));
            } else if (attachBlock.getBlockData() instanceof Bisected) { // 上下分方块
                subRelate.add(new BisectedRelate(null, attachBlock));
            } else if (attachBlock.getBlockData() instanceof Bed) { // 床
                subRelate.add(new BedRelate(null, attachBlock));
            } else { // 其他
                relateBlocks.add(attachBlock);
            }
        }
    }

}
