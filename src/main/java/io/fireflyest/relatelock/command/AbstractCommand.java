package io.fireflyest.relatelock.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.command.CommandSender;
import io.fireflyest.relatelock.command.args.Argument;

/**
 * 抽象指令
 * 
 * @author Fireflyest
 * @since 1.0
 */
public abstract class AbstractCommand {
    
    public static final int MAX_ARGS = 5;

    /**
     * 指令名称
     */
    protected String name;

    /**
     * 变量列表，最多设置五个变量
     */
    protected List<Argument> arguments = new ArrayList<>(MAX_ARGS);

    /**
     * 抽象指令
     * 
     * @param name 指令名称
     */
    protected AbstractCommand(@Nonnull String name) {
        this.name = name;
    }

    /**
     * 获取指令名称
     * 
     * @return 指令名称
     */
    public String getName() {
        return name;
    }

    /**
     * 添加变量
     * @param arg 变量
     * @return 本身
     */
    public AbstractCommand addArg(@Nonnull Argument arg) {
        if (arguments.size() < MAX_ARGS) {
            arguments.add(arg);
        }
        return this;
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

}
