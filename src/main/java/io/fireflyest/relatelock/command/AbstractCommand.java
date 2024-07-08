package io.fireflyest.relatelock.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import io.fireflyest.relatelock.command.args.Argument;

/**
 * 抽象指令
 * 
 * @author Fireflyest
 * @since 1.0
 */
public abstract class AbstractCommand {
    
    protected static final int MAX_ARGS = 5;

    /**
     * 变量列表，最多设置五个变量
     */
    private List<Argument> arguments = new ArrayList<>(MAX_ARGS);

    /**
     * 指令名称
     */
    private String name;

    /**
     * 是否异步执行
     */
    private boolean async;

    /**
     * 应用的插件
     */
    protected JavaPlugin plugin;

    /**
     * 抽象指令
     * 
     * @param name 指令名称
     */
    protected AbstractCommand(@Nullable String name) {
        this.name = name;
    }

    /**
     * 抽象指令，由类名决定名称
     */
    protected AbstractCommand() {
    }

    /**
     * 获取指令名称
     * 
     * @return 指令名称
     */
    public String getName() {
        if (name == null) {
            name = StringUtils.removeEnd(this.getClass().getSimpleName().toLowerCase(), "command");
        }
        return name;
    }

    /**
     * 获取指令的执行方式
     * 
     * @return 是否异步执行
     */
    public boolean isAsync() {
        return async;
    }

    /**
     * 设置指令为异步执行
     */
    public AbstractCommand async() {
        this.async = true;
        return this;
    }

    /**
     * 获取参数列表
     */
    public List<Argument> getArguments() {
        return arguments;
    }

    /**
     * 获取指令执行任务
     * 
     * @param sender 执行者
     * @param args 参数
     * @return 指令执行任务
     */
    protected CommandRunnable runnable(@Nonnull CommandSender sender, @Nonnull String[] args) {
        return new CommandRunnable(sender, args);
    }

    /**
     * 无参指令
     * @param sender 发送者
     * @return 是否正确
     */
    protected abstract boolean execute(@Nonnull CommandSender sender);

    /**
     * 单参指令
     * @param sender 发送者
     * @param arg1 参数1
     * @return 是否正确
     */
    protected boolean execute(@Nonnull CommandSender sender, 
                              @Nonnull String arg1) {
        return false;
    }

    /**
     * 
     * @param sender 发送者
     * @param arg1 参数1
     * @param arg2 参数2
     * @return 是否正确
     */
    protected boolean execute(@Nonnull CommandSender sender, 
                              @Nonnull String arg1, 
                              @Nonnull String arg2) {
        return false;
    }

    /**
     * 
     * @param sender 发送者
     * @param arg1 参数1
     * @param arg2 参数2
     * @param arg3 参数3
     * @return 是否正确
     */
    protected boolean execute(@Nonnull CommandSender sender, 
                              @Nonnull String arg1, 
                              @Nonnull String arg2, 
                              @Nonnull String arg3) {
        return false;
    }

    /**
     * 
     * @param sender 发送者
     * @param args 参数
     * @return 是否正确
     */
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        return false;
    }

    /**
     * 获取参数
     * @param index 参数位置
     * @param arg 参数
     * @return 提示列表
     */
    @Nullable
    protected List<String> getArgumentTab(int index, @Nonnull CommandSender sender, String arg) {
        if (index < 0) {
            index = 0;
        }
        final boolean hasArg = arguments.size() > index;
        return hasArg ? arguments.get(index).tab(sender, arg) : Collections.emptyList();
    }

    /**
     * 指令执行任务
     * @author Fireflyest
     * @since 1.0
     */
    protected class CommandRunnable extends BukkitRunnable {

        private final CommandSender sender;
        private final String[] args;

        private boolean valid = false;

        /**
         * 构造可执行指令任务
         * 
         * @param sender 指令发送者
         * @param args 参数
         */
        public CommandRunnable(@Nonnull CommandSender sender, @Nonnull String[] args) {
            this.sender = sender;
            this.args = args;
        }

        @Override
        public void run() {
            switch (args.length) {
                case 0:
                    valid = execute(sender);
                    break;
                case 1:
                    valid = execute(sender, args[0]);
                    break;
                case 2:
                    valid = execute(sender, args[0], args[1]);
                    break;
                case 3:
                    valid = execute(sender, args[0], args[1], args[2]);
                    break;
                default:
                    valid = execute(sender, args);
                    break;
            }
        }

        /**
         * 指令运行的有效性
         * 
         * @return 是否有效
         */
        public boolean isValid() {
            return valid;
        }

    }

}
