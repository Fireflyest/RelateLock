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
        this.permission = "lock.backup";
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        return this.execute(sender, "backup");
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        if (!sender.hasPermission(permission)) {
            return false;
        }
        locksmith.save(RelateLock.getPlugin(), arg1);
        return true;
    }

}
