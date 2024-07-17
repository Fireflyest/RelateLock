package io.fireflyest.relatelock.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import io.fireflyest.relatelock.bean.Lock;
import io.fireflyest.relatelock.config.Config;
import io.fireflyest.relatelock.core.LocksmithImpl;
import io.fireflyest.relatelock.util.YamlUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;

/**
 * 锁指令
 * 
 * @author Fireflyest
 * @since 1.0
 */
public class LockCommand extends ComplexCommand {

    private final LocksmithImpl locksmith;
    private final Config config;

    public LockCommand(@Nonnull LocksmithImpl locksmith, @Nonnull Config config) {
        this.locksmith = locksmith;
        this.config = config;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        if (sender instanceof Player player) {
            final Block block = player.getTargetBlockExact(5);
            Lock lock = null;
            if (block != null && (lock = locksmith.getLock(block.getLocation())) != null) {
                locksmith.trimLogs(lock);
                player.spigot().sendMessage(this.anPlayer("🔒", lock.getOwner()));
                player.spigot().sendMessage(this.type("类型:", lock.getType(), lock.getData()));
                player.spigot().sendMessage(this.listPlayers("共享:", lock.getShare()));
                if (player.getUniqueId().toString().equals(lock.getOwner())) {
                    player.spigot().sendMessage(this.listLogs("记录:", lock.getLog()));
                }
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
                            .event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, playerName))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, entity));
        }
        return componentBuilder.create();
    }

    /**
     * 发送玩家
     * @param title 开头
     * @param uid 玩家UUID
     * @return 玩家
     */
    private BaseComponent[] anPlayer(@Nonnull String title, @Nonnull String uid) {
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
     * 发送类型
     * 
     * @param title 开头
     * @param type 类型
     * @param data 类型数据
     * @return 类型
     */
    private BaseComponent[] type(@Nonnull String title, 
                                 @Nonnull String type, 
                                 @Nonnull String data) {
        
        final ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(title).append(" ");
        if (type.equals(config.lockString())) {
            componentBuilder.append("普通锁 ");
        } else if (type.equals(config.lockPasswordString())) {
            componentBuilder.append("密码锁 ")
                            .append("[")
                            .append("#".repeat(data.length()))
                            .append("]");
        } else if (type.equals(config.lockFeeString())) {
            componentBuilder.append("付费锁 [").append(data).append("]");
        } else if (type.equals(config.lockTokenString()) && data.startsWith(YamlUtils.DATA_PATH)) {
            final ItemStack token = YamlUtils.deserializeItemStack(data);
            final String material = token.getType().name().toLowerCase();
            final Item item = new Item("minecraft:" + material, token.getAmount(), null);
            componentBuilder.append("代币锁 [")
                            .append(new TranslatableComponent("item.minecraft." + material))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, item))
                            .append("×" + token.getAmount()).reset()
                            .append("]");
        }
        return componentBuilder.create();
    }

    /**
     * 发送操作记录
     * 
     * @param title 开头
     * @param logs 记录集合
     * @return 记录
     */
    private BaseComponent[] listLogs(@Nonnull String title, @Nonnull Set<String> logs) {
        final StringBuilder sBuilder = new StringBuilder();
        final List<String> logList = new ArrayList<>(logs);
        Collections.sort(logList);
        for (String log : logList) {
            sBuilder.append(log).append("\n");
        }
        final String logString = StringUtils.trim(sBuilder.toString());

        final ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(title);
        componentBuilder.append(" ")
                        .append(String.valueOf(logs.size()))
                        .event(new HoverEvent(Action.SHOW_TEXT, new Text(logString)));
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
