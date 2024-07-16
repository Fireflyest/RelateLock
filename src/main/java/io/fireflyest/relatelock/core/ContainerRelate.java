package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.data.Directional;
import io.fireflyest.relatelock.Print;
import io.fireflyest.relatelock.util.BlockUtils;

/**
 * 容器锁
 * @author Fireflyest
 * @since 1.0
 */
public class ContainerRelate extends Relate {

    private final boolean leftTrace;
    private final boolean rightTrace;
    private final boolean upTrace;

    protected ContainerRelate(@Nullable Block signBlock, @Nonnull Block attachBlock, int deep) {
        this(signBlock, attachBlock, deep, false, false, false);
    }

    /**
     * 容器附近关联追溯
     * 
     * @param signBlock 牌子
     * @param attachBlock 附着方块
     * @param deep 关联深度
     * @param trace 追溯
     */
    protected ContainerRelate(@Nullable Block signBlock, 
                              @Nonnull Block attachBlock, 
                              int deep,
                              boolean trace) {
        super(signBlock, attachBlock, deep);
        this.leftTrace = trace;
        this.rightTrace = trace;
        this.upTrace = trace;
    }

    /**
     * 容器附近关联追溯
     * 
     * @param signBlock 牌子
     * @param attachBlock 附着方块
     * @param deep 关联深度
     * @param leftTrace 向左追溯
     * @param rightTrace 向右追溯
     * @param upTrace 向上追溯
     */
    protected ContainerRelate(@Nullable Block signBlock, 
                              @Nonnull Block attachBlock, 
                              int deep,
                              boolean leftTrace,
                              boolean rightTrace,
                              boolean upTrace) {
        super(signBlock, attachBlock, deep);
        this.leftTrace = leftTrace;
        this.rightTrace = rightTrace;
        this.upTrace = upTrace;
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getState() instanceof Container) {
            // 容器本身
            Print.RELATE_LOCK.debug("[{}]ContainerRelate.traceRelateBlocks() -> container", deep);
            relateBlocks.add(attachBlock);

            if (attachBlock.getBlockData() instanceof Directional directional && deep < 1) {
                if (leftTrace) {
                    final BlockFace leftFace = BlockUtils.leftFace(directional.getFacing());
                    final Block leftBlock = attachBlock.getRelative(leftFace);
                    subRelate.add(new ContainerRelate(null, leftBlock, this.deep + 1, 
                                                      true, 
                                                      false, 
                                                      false));
                }
                if (rightTrace) {
                    final BlockFace rightFace = BlockUtils.rightFace(directional.getFacing());
                    final Block rightBlock = attachBlock.getRelative(rightFace);
                    subRelate.add(new ContainerRelate(null, rightBlock, this.deep + 1, 
                                                      false, 
                                                      true, 
                                                      false));
                }
                if (upTrace) {
                    final Block rightBlock = attachBlock.getRelative(BlockFace.UP);
                    subRelate.add(new ContainerRelate(null, rightBlock, this.deep + 1, true));
                }
            }
        }
    }
    
}
