package io.fireflyest.relatelock.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 复杂指令
 * 
 * @author Fireflyest
 * @since 1.0
 */
public abstract class ComplexCommand extends AbstractCommand 
                                     implements CommandExecutor, TabCompleter {

    /**
     * 子指令
     */
    protected final Map<String, SubCommand> subCommands = new HashMap<>();

    /**
     * 复杂指令
     * 
     * @param name 指令名称
     */
    protected ComplexCommand(@Nullable String name) {
        super(name);

        // 第一个参数的提示为子指令
        this.arguments.add((sender, arg) -> {
            final List<String> argList = new ArrayList<>();
            for (String string : subCommands.keySet()) {
                if (string.startsWith(arg)) {
                    argList.add(string);
                }
            }
            return argList;
        });
    }

    /**
     * 复杂指令
     */
    protected ComplexCommand() {
        this(null);
    }

    @Override
    public boolean onCommand(CommandSender sender, 
                             Command command, 
                             String label, 
                             String[] args) {
        SubCommand subCommand = null;
        if (args.length == 0) {
            return execute(sender);
        } else if ((subCommand = subCommands.get(args[0])) != null) {
            final String[] subArgs = new String[args.length - 1];
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);
            switch (subArgs.length) {
                case 0:
                    return subCommand.execute(sender);
                case 1:
                    return subCommand.execute(sender, subArgs[0]);
                case 2:
                    return subCommand.execute(sender, subArgs[0], subArgs[1]);
                case 3:
                    return subCommand.execute(sender, subArgs[0], subArgs[1], subArgs[2]);
                default:
                    return subCommand.execute(sender, subArgs);
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, 
                                      Command command, 
                                      String label, 
                                      String[] args) {
        int index = args.length - 1;
        List<String> list = Collections.emptyList();
        SubCommand subCommand = null;
        if (index == 0) { // 复杂指令的子指令列表
            list = this.getArgumentTab(index, sender, args[index]);
        } else if ((subCommand = subCommands.get(args[0])) != null) { // 转由子指令提示
            index--;
            list = subCommand.getArgumentTab(index, sender, args[index]);
        }
        return list;
    }

    /**
     * 添加子指令
     * 
     * @param subCommand 子指令
     */
    public ComplexCommand addSub(@Nonnull SubCommand subCommand) {
        final String subCommandName = subCommand.getName();
        this.subCommands.put(StringUtils.removeStart(subCommandName, this.getName()), subCommand);
        return this;
    }

    /**
     * 应用到插件
     * 
     * @param plugin 插件
     */
    public AbstractCommand apply(@Nonnull JavaPlugin plugin) {
        final PluginCommand command = plugin.getCommand(this.getName());
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
        return this;
    }

}
