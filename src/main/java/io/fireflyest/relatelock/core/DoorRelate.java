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

    protected DoorRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getState().getBlockData() instanceof Door door) {
            // 自己
            Print.RELATE_LOCK.debug("DoorRelate.traceRelateBlocks() -> door");
            subRelate.add(new BisectedRelate(null, attachBlock));

            // 另一扇门
            final Block anotherDoor = switch (door.getHinge()) {
                case LEFT -> attachBlock.getRelative(BlockUtils.rightFace(door.getFacing()));
                case RIGHT -> attachBlock.getRelative(BlockUtils.leftFace(door.getFacing()));
                default -> null;
            };
            if (anotherDoor != null && anotherDoor.getState().getBlockData() instanceof Door) {
                Print.RELATE_LOCK.debug("DoorRelate.traceRelateBlocks() -> door");
                subRelate.add(new BisectedRelate(null, anotherDoor));
            }
        }
    }

}
