package io.fireflyest.relatelock.command;

import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;
import io.fireflyest.relatelock.RelateLock;
import io.fireflyest.relatelock.core.LocksmithImpl;

/**
 * 锁数据备份
 * @author Fireflyest
 * @since 1.0
 */
public class LockBackupCommand extends SubCommand {

    private final LocksmithImpl locksmith;

    public LockBackupCommand(LocksmithImpl locksmith) {
        this.locksmith = locksmith;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        locksmith.save(RelateLock.getPlugin(), "backup");
        return true;
    }
    
}
