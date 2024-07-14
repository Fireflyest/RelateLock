package io.fireflyest.relatelock.command;

import javax.annotation.Nonnull;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.fireflyest.relatelock.bean.Lock;
import io.fireflyest.relatelock.config.Config;
import io.fireflyest.relatelock.core.api.Locksmith;
import io.fireflyest.relatelock.util.YamlUtils;

/**
 * 密码锁
 * @author Fireflyest
 * @since 1.0
 */
public class LockDataCommand extends SubCommand {

    private final Locksmith locksmith;
    private final Config config;

    public LockDataCommand(@Nonnull Locksmith locksmith, @Nonnull Config config) {
        this.locksmith = locksmith;
        this.config = config;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        if (sender instanceof Player player) {
            final Block block = player.getTargetBlockExact(5);
            Lock lock = null;
            if (block != null && (lock = locksmith.getLock(block.getLocation())) != null
                              && lock.getOwner().equals(player.getUniqueId().toString())
                              && lock.getType().equals(config.lockTokenString())) {
                final String data = YamlUtils.serialize(player.getInventory().getItemInMainHand());
                lock.setData(data);
            }
        }
        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        if (sender instanceof Player player) {
            final Block block = player.getTargetBlockExact(5);
            Lock lock = null;
            if (block != null && (lock = locksmith.getLock(block.getLocation())) != null
                              && lock.getOwner().equals(player.getUniqueId().toString())) {
                lock.setData(arg1);
            }
        }
        return true;
    }
    
}
