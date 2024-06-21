package io.fireflyest.relatelock.core;

import java.util.Set;

import org.bukkit.block.Block;

public class BlockRelate extends Relate {

    protected BlockRelate(Block signBlock, Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public Relate traceRelateBlocks() {
        return this;
    }

    
}
