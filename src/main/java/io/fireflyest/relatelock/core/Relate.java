package io.fireflyest.relatelock.core;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;

/**
 * 方块之间的关联
 * @author Fireflyest
 * @since 1.0
 */
public abstract class Relate {
    
    protected final Block signBlock;
    protected final Block attachBlock;
    protected final Set<Block> relateBlocks = new HashSet<>();
    protected final Set<Relate> subRelate = new HashSet<>();

    protected boolean trace = false;

    /**
     * 方块关联实例化
     * @param signBlock 牌子方块
     * @param attachBlock 被贴的方块
     */
    protected Relate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        this.signBlock = signBlock;
        this.attachBlock = attachBlock;
    }

    /**
     * 追溯所有的关联，并把关联方块添加到relateBlocks中，
     * 将子关联添加到subRelate中
     */
    public abstract void traceRelateBlocks();

    /**
     * 获取相关联的方块，包括牌子本身及其所有子关联
     * @return 与牌子关联的方块集合
     */
    public Set<Block> getRelateBlocks() {
        if (!trace) {
            this.traceRelateBlocks(); // 追溯
            for (Relate relate : subRelate) { // 子关联
                relateBlocks.addAll(relate.getRelateBlocks());
            }
            trace = true;
        }
        return relateBlocks;
    }

    /**
     * 获取牌子方块，子关联的牌子方块为null
     * @return 牌子方块
     */
    @Nullable
    public Block getSignBlock() {
        return signBlock;
    }

}
