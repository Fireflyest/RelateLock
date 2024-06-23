package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;

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
        if (attachBlock.getBlockData() instanceof Door door) {
            // 自己
            subRelate.add(new BisectedRelate(null, attachBlock));
            // 另一扇门
            final BlockFace face = switch (door.getHinge()) {
                case LEFT -> BlockUtils.rightFace(door.getFacing());
                case RIGHT -> BlockUtils.leftFace(door.getFacing());
                default -> null;
            };
            if (face != null) {
                final Block otherDoor = attachBlock.getRelative(face);
                subRelate.add(new BisectedRelate(null, otherDoor));
            }
        }
    }

}
