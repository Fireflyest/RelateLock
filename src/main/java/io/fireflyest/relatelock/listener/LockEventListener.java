package io.fireflyest.relatelock.listener;

import java.time.Instant;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    /**
     * 牌子内容改变
     * 
     * @param event 事件
     */
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final String[] lines = event.getLines();
        final Block block = event.getBlock();
        final String uid = player.getUniqueId().toString();

        // 是否新锁
        Lock lock = null;
        if (lines[0].equalsIgnoreCase(config.lockString())) {
            lock = new Lock(uid, Instant.now().toEpochMilli(), "normal", null);
        } else {
            final String[] firstLine = StringUtils.split(lines[0], " ");
            final String type = firstLine[0];
            final String data = firstLine.length > 1 ? firstLine[1] : "123";
            if (type.equalsIgnoreCase(config.lockPasswordString())
                    || type.equalsIgnoreCase(config.lockFeeString())
                    || type.equalsIgnoreCase(config.lockTokenString())) {
                lock = new Lock(uid, Instant.now().toEpochMilli(), type, data);
            }
        }

        // 新锁上锁
        if (lock != null) {
            final boolean result = locksmith.lock(event.getBlock(), lock);
            if (result) {
                player.playSound(player, Sound.BLOCK_IRON_DOOR_CLOSE, 1, 1);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                                            new ComponentBuilder("已上锁").create());
            }
        }

        // 更新已上锁牌子文本
        if (locksmith.isLocationLocked(block.getLocation())) {
            // 上锁牌子修改
            final String[] newLines = locksmith.signChange(block.getLocation(), uid, lines);
            for (int i = 0; i < 4; i++) {
                event.setLine(i, newLines[i]);
            }
        }
    }

    /**
     * 玩家右键
     * @param event 事件
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if (block == null || event.getAction() != Action.RIGHT_CLICK_BLOCK 
                          || !locksmith.isLocationLocked(block.getLocation())) {
            return;
        }

        // 防止操作门的时候使用手上物品
        if (block.getBlockData() instanceof Openable) {
            event.setUseItemInHand(Result.DENY);
        }

        // 冷却防止重复点击
        if (lockOrganism.exist(block.getLocation())) {
            return;
        }
        lockOrganism.setex(block.getLocation(), 5, cooldownLock);

        // 权限判断
        final Player player = event.getPlayer();
        final boolean result = locksmith.use(block.getLocation(), player);
        if (!result) {
            event.getPlayer().spigot()
                .sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("已被上锁")
                .create());
            event.setCancelled(true);
        }

        // 查看锁
        if (event.getPlayer().isSneaking()) {
            player.performCommand("lock");
        }

    }

    /**
     * 玩家破坏方块
     * 
     * @param event 事件
     */
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

    /**
     * 玩家放置方块
     * 
     * @param event 事件
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.isCancelled()) {
            return;
        }

        final Block block = event.getBlock();
        final String uid = event.getPlayer().getUniqueId().toString();
        
        if (!locksmith.place(block, uid)) {
            event.getPlayer().spigot()
                 .sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("无法放置")
                 .create());
            event.setCancelled(true);
        }

    }

    /**
     * 方块爆炸
     * 
     * @param event 事件
     */
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> locksmith.isLocationLocked(block.getLocation()));
    }

    /**
     * 生物爆炸
     * 
     * @param event 事件
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> locksmith.isLocationLocked(block.getLocation()));
    }

}
