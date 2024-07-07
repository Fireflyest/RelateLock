package io.fireflyest.relatelock.command;

import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;
import io.fireflyest.relatelock.core.api.Locksmith;

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
        
        return true;
    }

 
    
}
