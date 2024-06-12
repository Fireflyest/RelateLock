package io.fireflyest.relatelock.api;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.block.Container;

public interface Locksmith {
    
    boolean canLock(@Nonnull Block block);

    boolean lock(@Nonnull Block block);

    boolean lockContainer(@Nonnull Container container);

}
