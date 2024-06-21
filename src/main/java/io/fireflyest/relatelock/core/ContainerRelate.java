package io.fireflyest.relatelock.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.block.Block;

public class ContainerRelate extends Relate {

    protected ContainerRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
        //TODO Auto-generated constructor stub
    }

    @Override
    public Relate traceRelateBlocks() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'traceRelateBlocks'");
    }
    
}
