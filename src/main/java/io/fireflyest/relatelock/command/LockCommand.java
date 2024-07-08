package io.fireflyest.relatelock.command;

import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.fireflyest.relatelock.bean.Lock;
import io.fireflyest.relatelock.core.api.Locksmith;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Entity;

/**
 * 锁指令
 * 
 * @author Fireflyest
 * @since 1.0
 */
public class LockCommand extends ComplexCommand {

    private final Locksmith locksmith;

    public LockCommand(Locksmith locksmith) {
        this.locksmith = locksmith;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        if (sender instanceof Player player) {
            final Block block = player.getTargetBlockExact(5);
            Lock lock = null;
            if (block != null && (lock = locksmith.getLock(block.getLocation())) != null) {
                player.spigot().sendMessage(this.sendPlayer("🔒", lock.getOwner()));
                player.spigot().sendMessage(this.listPlayers("管理:", lock.getManager()));
                player.spigot().sendMessage(this.listPlayers("共享:", lock.getShare()));
                player.sendMessage("记录: " + lock.getLog().size());
            }
        }

        return true;
    }

 
    /**
     * 列出所有玩家
     * @param title 开头
     * @param playerSet 玩家集合
     * @return 玩家列表
     */
    private BaseComponent[] listPlayers(@Nonnull String title, @Nonnull Set<String> playerSet) {
        final ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(title);
        for (String uid : playerSet) {
            final String playerName = this.getPlayerName(uid);
            final Entity entity = new Entity("minecraft:player", 
                                                  uid, 
                                                  new TextComponent(playerName));
            componentBuilder.append(" ")
                            .append(playerName)
                            .event(new HoverEvent(Action.SHOW_ENTITY, entity));
        }
        return componentBuilder.create();
    }

    /**
     * 发送玩家
     * @param title 开头
     * @param uid 玩家UUID
     * @return 玩家
     */
    private BaseComponent[] sendPlayer(@Nonnull String title, @Nonnull String uid) {
        final ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(title);
        final String playerName = this.getPlayerName(uid);
        final Entity entity = new Entity("minecraft:player", 
                                                uid, 
                                                new TextComponent(playerName));
        componentBuilder.append(" ")
                        .append(playerName)
                        .bold(true)
                        .event(new HoverEvent(Action.SHOW_ENTITY, entity));
        return componentBuilder.create();
    }

    /**
     * 根据玩家UUID获取玩家名称
     * @param uid 玩家UUID
     * @return 玩家名称
     */
    private String getPlayerName(String uid) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uid));
        return offlinePlayer.getName();
    }

}
