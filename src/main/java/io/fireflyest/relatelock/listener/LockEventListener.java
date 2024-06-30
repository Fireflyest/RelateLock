package io.fireflyest.relatelock.listener;

import java.time.Instant;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import io.fireflyest.relatelock.bean.Lock;
import io.fireflyest.relatelock.cache.LockOrganism;
import io.fireflyest.relatelock.config.Config;
import io.fireflyest.relatelock.core.api.Locksmith;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 * 锁事件
 * @author Fireflyest
 * @since 1.0
 */
public class LockEventListener implements Listener {

    private final Lock cooldownLock = new Lock();
    private final LockOrganism lockOrganism = new LockOrganism("cooldown");

    private final Locksmith locksmith;
    private final Config config;

    public LockEventListener(Locksmith locksmith, Config config) {
        this.locksmith = locksmith;
        this.config = config;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final String[] lines = event.getLines();
        // 0-3
        if (config.lockString().equals(lines[0])) {
            final String uid = player.getUniqueId().toString();
            final Lock lock = new Lock(uid, Instant.now().toEpochMilli(), "normal", null);
            final boolean result = locksmith.lock(event.getBlock(), lock);
            if (result) {
                event.setLine(0, "🔒 §l" + player.getName());
                player.playSound(player, Sound.BLOCK_IRON_DOOR_CLOSE, 1, 1);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                                            new ComponentBuilder("已上锁").create());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // if (event.useInteractedBlock() == Result.DENY) {
        //     return;
        // }

        final Block block = event.getClickedBlock();
        if (block == null || event.getAction() != Action.RIGHT_CLICK_BLOCK 
                          || !locksmith.isLocationLocked(block.getLocation())) {
            return;
        }

        if (block.getState() instanceof TileState || block.getBlockData() instanceof Door) {
            if (lockOrganism.exist(block.getLocation())) {
                return;
            }
            lockOrganism.setex(block.getLocation(), 2, cooldownLock);

            final Player player = event.getPlayer();
            final String uid = player.getUniqueId().toString();
            final boolean result = locksmith.use(block.getLocation(), uid, player.getName());
            if (result) {
                event.getPlayer().spigot()
                     .sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("允许使用")
                     .create());
            } else {
                event.getPlayer().spigot()
                     .sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("已被上锁")
                     .create());
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.isCancelled()) {
            return;
        }

        final Location location = event.getBlock().getLocation();

        // 如果是有锁的方块
        if (locksmith.isLocationLocked(location)) {
            final Player player = event.getPlayer();
            final String uid = player.getUniqueId().toString();
            final boolean result = locksmith.destroy(location, uid, player.getName());
            if (result) {
                event.getPlayer().spigot()
                     .sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("已解除锁")
                     .create());
            } else {
                event.getPlayer().spigot()
                     .sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("已被上锁")
                     .create());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        final Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            final Block block = iterator.next();
            if (locksmith.isLocationLocked(block.getLocation())) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        final Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            final Block block = iterator.next();
            if (locksmith.isLocationLocked(block.getLocation())) {
                iterator.remove();
            }
        }
    }

    // @EventHandler
    // public void onBlockPiston(BlockPistonEvent event) {
    //     final Block block = event.getBlock();
    //     if (locksmith.isLocationLocked(block.getLocation())) {
    //         event.setCancelled(true);
    //     }
    // }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        // for (BlockState tileEntitie : event.getChunk().getTileEntities()) {
        //     System.out.println(tileEntitie.getType().name());
        // }
    }

}
