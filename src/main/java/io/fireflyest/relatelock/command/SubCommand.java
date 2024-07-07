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

    protected SubCommand(@Nonnull String name, @Nullable String permission) {
        super(name);
        this.permission = permission;
    }

    protected SubCommand(@Nonnull String name) {
        this(name, null);
    }

    @Override
    public SubCommand addArg(@Nonnull Argument arg) {
        super.addArg(arg);
        return this;
    }

}
