package io.fireflyest.relatelock.command;

import javax.annotation.Nonnull;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.fireflyest.relatelock.core.LocksmithImpl;

/**
 * 解锁指令
 * 
 * @author Fireflyest
 * @since 1.0
 */
public class UnlockCommand extends SimpleCommand {

    private final LocksmithImpl locksmith;

    public UnlockCommand(LocksmithImpl locksmith) {
        this.locksmith = locksmith;
    }

    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        if (sender instanceof Player player) {
            final Block block = player.getTargetBlockExact(5);
            final Location signLocation = block.getLocation();
            if (block != null && locksmith.getLocationOrg().scard(signLocation) > 1) {
                locksmith.unlock(signLocation);
                locksmith.getLockOrg().del(signLocation);
                block.breakNaturally();
            }
        }
        return true;
    }
    
}
