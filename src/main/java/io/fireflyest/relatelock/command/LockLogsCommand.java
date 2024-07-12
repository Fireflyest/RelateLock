package io.fireflyest.relatelock.command;

import javax.annotation.Nonnull;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.fireflyest.relatelock.bean.Lock;
import io.fireflyest.relatelock.core.LocksmithImpl;

/**
 * 锁操作记录查看
 * 
 * @author Fireflyest
 * @since 1.0
 */
public class LockLogsCommand extends SubCommand {

    private final LocksmithImpl locksmith;

    public LockLogsCommand(LocksmithImpl locksmith) {
        this.locksmith = locksmith;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        if (sender instanceof Player player) {
            final Block block = player.getTargetBlockExact(5);
            Lock lock = null;
            if (block != null && (lock = locksmith.getLock(block.getLocation())) != null) {
                locksmith.trimLogs(lock);
                for (String log : lock.getLog()) {
                    player.sendMessage(log);
                }
            }
        }
        return true;
    }
    
}
