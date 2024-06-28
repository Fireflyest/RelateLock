package io.fireflyest.relatelock.listener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Lockable;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import io.fireflyest.relatelock.bean.Lock;
import io.fireflyest.relatelock.config.Config;
import io.fireflyest.relatelock.core.api.Locksmith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LockEventListener implements Listener {

    private final Locksmith locksmith;
    private final Config config;

    public LockEventListener(Locksmith locksmith, Config config) {
        this.locksmith = locksmith;
        this.config = config;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event){
        
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
                                            new ComponentBuilder("成功给该容器上锁").create());
            }
        }
        // // 判断是否容器
        // Directional data = (Directional)event.getBlock().getBlockData();
        // Block block = event.getBlock().getLocation().add(data.getFacing().getOppositeFace().getDirection()).getBlock();
        // if(!(block.getState() instanceof Container))return;
        // Container container = (Container)block.getState();

        // String name = event.getPlayer().getName();

        // // 判断是否给容器上锁
        // if("lock".equalsIgnoreCase(lines[0])) {
        //     // 上锁
        //     if(LockUtils.addLock(container, name, lines[2], lines[3])){
                // event.setLine(0, "§f[§aLock§f]");
                // event.setLine(1, name);
                // event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("成功给该容器上锁").create());
        //     }else {
        //         event.setLine(0, "§f[§cError§f]");
        //         event.setLine(1, "容器已被锁");
        //         event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("容器上锁失败").create());
        //     }
        // }else if("unlock".equalsIgnoreCase(lines[0])) {
        //     if("".equals(container.getLock())) return;
        //     Lock lock = LockUtils.gsonDeserializer.fromJson(container.getLock(), Lock.class);
        //     if(lock.getOwner().equals(name)){
        //         event.getBlock().breakNaturally();
        //         LockUtils.unlock(container);
        //         event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("容器锁去除").create());
        //     }else {
        //         event.setLine(0, "§f[§cError§f]");
        //         event.setLine(1, "你无权解锁容器");
        //     }
        // }else if("friend".equalsIgnoreCase(lines[0])) {
        //     if("".equals(container.getLock())) return;
        //     Lock lock = LockUtils.gsonDeserializer.fromJson(container.getLock(), Lock.class);
        //     if(lock.getOwner().equals(name)){
        //         event.setLine(0, "§f[§aFriend§f]");
        //         List<String> friendList = new ArrayList<>(Arrays.asList(lock.getFriend()));
        //         if(!"".equals(lines[1])) friendList.add(lines[1]);
        //         if(!"".equals(lines[2])) friendList.add(lines[2]);
        //         if(!"".equals(lines[3])) friendList.add(lines[3]);
        //         lock.setFriend(friendList.toArray(new String[0]));
        //         LockUtils.unlock(container);
        //         LockUtils.addLock(container, lock);
        //         event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("容器添加共享，Shift+右键查看").create());
        //     }else {
        //         event.setLine(0, "§f[§cError§f]");
        //         event.setLine(1, "你无权添加共享");
        //     }
        // }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (event.useInteractedBlock() == Result.DENY) {
            return;
        }

        final Block block = event.getClickedBlock();
        if (block != null && locksmith.isLocationLocked(block.getLocation()) 
                          && block.getState() instanceof TileState) {
            final Player player = event.getPlayer();
            final String uid = player.getUniqueId().toString();
            final boolean result = locksmith.use(block.getLocation(), uid, player.getName());
            if (result) {
                // TODO: 开门
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
    public void onBlockBreak(BlockBreakEvent event){

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

    @EventHandler
    public void onBlockPiston(BlockPistonEvent event) {
        final Block block = event.getBlock();
        if (locksmith.isLocationLocked(block.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {



        // for (BlockState tileEntitie : event.getChunk().getTileEntities()) {
        //     System.out.println(tileEntitie.getType().name());
        // }
        
    }

}
