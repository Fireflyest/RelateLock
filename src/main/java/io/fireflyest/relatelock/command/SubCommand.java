package io.fireflyest.relatelock.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import io.fireflyest.relatelock.command.args.Argument;

/**
 * 子指令
 * 
 * @author Fireflyest
 * @since 1.0
 */
public abstract class SubCommand extends AbstractCommand {

    /**
     * 子指令权限
     */
    protected final String permission;

    /**
     * 子指令
     * 
     * @param name 名称
     * @param permission 权限
     */
    protected SubCommand(@Nullable String name, @Nullable String permission) {
        super(name);
        this.permission = permission;
    }

    /**
     * 子指令
     * 
     * @param name 名称
     */
    protected SubCommand(@Nullable String name) {
        this(name, null);
    }

    /**
     * 子指令
     */
    protected SubCommand() {
        this(null);
    }

    /**
     * 添加变量
     * @param arg 变量
     * @return 本身
     */
    public SubCommand addArg(@Nonnull Argument arg) {
        if (arguments.size() < MAX_ARGS) {
            arguments.add(arg);
        }
        return this;
    }

}
