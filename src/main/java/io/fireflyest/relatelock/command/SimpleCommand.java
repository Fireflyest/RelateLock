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
import io.fireflyest.relatelock.command.args.Argument;

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
    protected SimpleCommand(@Nullable String name) {
        super(name);
    }

    /**
     * 简单指令
     */
    protected SimpleCommand() {
        super();
    }

    @Override
    public boolean onCommand(CommandSender sender, 
                             Command command, 
                             String label, 
                             String[] args) {
        
        return this.executeCommand(sender, args);
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

    @Override
    public SimpleCommand async() {
        super.async();
        return this;
    }

    /**
     * 添加变量
     * 
     * @param arg 变量
     * @return 本身
     */
    public SimpleCommand addArg(@Nonnull Argument arg) {
        final List<Argument> arguments = this.getArguments();
        if (arguments.size() < MAX_ARGS) {
            arguments.add(arg);
        }
        return this;
    }

    /**
     * 应用到插件
     * 
     * @param plugin 插件
     */
    public SimpleCommand apply(@Nonnull JavaPlugin plugin) {
        this.plugin = plugin;
        final PluginCommand command = plugin.getCommand(this.getName());
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
        return this;
    }

    /**
     * 执行指令
     * @param sender 执行者
     * @param args 参数
     * @return 执行是否有效
     */
    private boolean executeCommand(@Nonnull CommandSender sender, @Nonnull String[] args) {
        boolean valid = false;
        final CommandRunnable runnable = this.runnable(sender, args);
        if (this.isAsync() && plugin != null) {
            runnable.runTaskAsynchronously(plugin);
            valid = true;
        } else {
            runnable.run();
            valid = runnable.isValid();
        }
        return valid;
    }
    
}
