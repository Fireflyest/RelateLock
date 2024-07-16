package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import io.fireflyest.relatelock.Print;

/**
 * 床
 * @author Fireflyest
 * @since 1.0
 */
public class BedRelate extends Relate {

    protected BedRelate(@Nullable Block signBlock, @Nonnull Block attachBlock, int deep) {
        super(signBlock, attachBlock, deep);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getState().getBlockData() instanceof Bed bed) {
            // 自己
            Print.RELATE_LOCK.debug("[{}]BedRelate.traceRelateBlocks() -> half", deep);
            relateBlocks.add(attachBlock);

            // 另一部分，床头是朝向
            final Block otherPart = switch (bed.getPart()) {
                case HEAD -> attachBlock.getRelative(bed.getFacing().getOppositeFace());
                case FOOT -> attachBlock.getRelative(bed.getFacing());
                default -> null;
            };
            if (otherPart != null && otherPart.getState().getBlockData() instanceof Bed) {
                Print.RELATE_LOCK.debug("[{}]BedRelate.traceRelateBlocks() -> half", deep);
                relateBlocks.add(otherPart);
            }
        }
    }
    
}
