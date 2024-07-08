package io.fireflyest.relatelock.command;

import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;
import io.fireflyest.relatelock.core.api.Locksmith;

/**
 * 密码锁
 * @author Fireflyest
 * @since 1.0
 */
public class LockPwdCommand extends SubCommand {

    private final Locksmith locksmith;

    public LockPwdCommand(Locksmith locksmith) {
        this.locksmith = locksmith;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {

        return true;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        return super.execute(sender, arg1);
    }
    
}
