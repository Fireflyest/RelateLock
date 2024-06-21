package io.fireflyest.relatelock.core;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.block.Block;

public class DoorRelate extends Relate {

    protected DoorRelate(@Nullable Block signBlock, @Nonnull Block attachBlock) {
        super(signBlock, attachBlock);
    }

    @Override
    public Relate traceRelateBlocks() {
        return this;
    }

}
