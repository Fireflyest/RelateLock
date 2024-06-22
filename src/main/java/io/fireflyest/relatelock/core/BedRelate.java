package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;

/**
 * 床
 * @author Fireflyest
 * @since 1.0
 */
public class BedRelate extends Relate {

    protected BedRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getBlockData() instanceof Bed bed) {
            // 自己
            relateBlocks.add(attachBlock);
            // 另一部分
            final Directional directional = ((Directional) attachBlock.getState());
            final BlockFace face = switch (bed.getPart()) {
                case HEAD -> directional.getFacing();
                case FOOT -> directional.getFacing().getOppositeFace();
                default -> null;
            };
            if (face != null) {
                final Block otherPart = attachBlock.getRelative(face);
                // 某些情况下床只有一半
                if (attachBlock.getBlockData() instanceof Bed) {
                    relateBlocks.add(otherPart);
                }
            }
        }
    }
    
}
