package io.fireflyest.relatelock.command;

import java.util.List;
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
    protected String permission;

    /**
     * 子指令
     * 
     * @param name 名称
     */
    protected SubCommand(@Nullable String name) {
        super(name);
    }

    /**
     * 子指令
     */
    protected SubCommand() {
        super();
    }

    @Override
    public SubCommand async() {
        super.async();
        return this;
    }

    /**
     * 添加变量
     * 
     * @param arg 变量
     * @return 本身
     */
    public SubCommand addArg(@Nonnull Argument arg) {
        final List<Argument> arguments = this.getArguments();
        if (arguments.size() < MAX_ARGS) {
            arguments.add(arg);
        }
        return this;
    }

}
