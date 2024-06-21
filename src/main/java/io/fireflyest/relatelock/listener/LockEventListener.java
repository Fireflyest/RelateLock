package io.fireflyest.relatelock.listener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Lockable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LockEventListener implements Listener {


    @EventHandler
    public void onSignChange(SignChangeEvent event){
        // if(event.isCancelled())return;
        // String[] lines = event.getLines();

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
        //         event.setLine(0, "§f[§aLock§f]");
        //         event.setLine(1, name);
        //         event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("成功给该容器上锁").create());
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
        System.out.println(event.getClickedBlock().getBlockData().getAsString());
        // if(!event.hasBlock()
        //         || event.getClickedBlock() == null
        //         || !(event.getClickedBlock().getState() instanceof Container))return;
        // Container container = (Container)event.getClickedBlock().getState();
        // LockUtils.use(container, event.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        // Block block;
        // if (event.getBlock().getState() instanceof Container){
        //     block = event.getBlock();
        // }else if(event.getBlock().getLocation().add(0, 1, 0).getBlock().getState() instanceof Lockable){
        //     block = event.getBlock().getLocation().add(0, 1, 0).getBlock();
        // }else {
        //     return;
        // }
        // Lockable lockable = (Lockable)block.getState();
        // if(!lockable.isLocked())return;
        // Lock lock = LockUtils.gsonDeserializer.fromJson(lockable.getLock(), Lock.class);
        // if(!lock.getOwner().equalsIgnoreCase(event.getPlayer().getName())){
        //     event.setCancelled(true);
        //     event.getPlayer().sendMessage(Language.TITLE + "容器已被锁");
        // }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (BlockState tileEntitie : event.getChunk().getTileEntities()) {
            System.out.println(tileEntitie.getType().name());
        }
        
    }

}
