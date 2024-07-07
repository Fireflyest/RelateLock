package io.fireflyest.relatelock.command;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 简单指令
 * 
 * @author Fireflyest
 * @since 
 */
public abstract class SimpleCommand extends AbstractCommand 
                                    implements CommandExecutor, TabCompleter {

    /**
     * 简单指令
     * 
     * @param name 名称
     */
    protected SimpleCommand(@Nonnull String name) {
        super(name);
    }

    @Override
    public boolean onCommand(CommandSender sender, 
                             Command command, 
                             String label, 
                             String[] args) {
        switch (args.length) {
            case 0:
                return execute(sender);
            case 1:
                return execute(sender, args[0]);
            case 2:
                return execute(sender, args[0], args[1]);
            case 3:
                return execute(sender, args[0], args[1], args[2]);
            default:
                return execute(sender, args);
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(CommandSender sender, 
                                      Command command, 
                                      String label, 
                                      String[] args) {
        final int index = args.length - 1;
        return this.getArgumentTab(index, sender, args[index]);
    }

    /**
     * 应用到插件
     * 
     * @param plugin 插件
     */
    public void apply(@Nonnull JavaPlugin plugin) {
        final PluginCommand command = plugin.getCommand(name);
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }
    
}
