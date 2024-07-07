package io.fireflyest.relatelock.command.args;

import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;

/**
 * 指令参数
 * 
 * @author Fireflyest
 * @since 1.0
 */
public interface Argument {
    
    /**
     * 调用tab提示
     * @param sender 指令发送者
     * @param arg 正在编辑的参数
     * @return 提示列表
     */
    List<String> tab(@Nonnull CommandSender sender, @Nonnull String arg);

}
