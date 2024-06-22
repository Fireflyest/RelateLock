package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * 箱子锁
 * @author Fireflyest
 * @since 1.0
 */
public class ChestRelate extends Relate {

    protected ChestRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public void traceRelateBlocks() {
        if (attachBlock.getState() instanceof Container container) {
            if (container.getInventory() instanceof DoubleChestInventory doubleChestInventory) {
                // 大箱子的两边
                final InventoryHolder leftHolder = doubleChestInventory.getLeftSide().getHolder();
                final InventoryHolder rightHolder = doubleChestInventory.getRightSide().getHolder();
                if (leftHolder instanceof Block block) {
                    subRelate.add(new ContainerRelate(null, block));
                }
                if (rightHolder instanceof Block block) {
                    subRelate.add(new ContainerRelate(null, block));
                }
            } else {
                // 小箱子
                subRelate.add(new ContainerRelate(null, attachBlock));
            }
        }
    }
    
}
