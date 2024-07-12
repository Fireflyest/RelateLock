package io.fireflyest.relatelock.command.args;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;

/**
 * 任意参数
 * 
 * @author Fireflyest
 * @since 1.0
 */
public final class ObjectArgs implements Argument {

    /**
     * 提示列表
     */
    private final List<Object> objects;

    /**
     * 使用提示列表创建
     * 
     * @param objects 提示列表
     */
    public ObjectArgs(List<Object> objects) {
        this.objects = objects;
    }

    /**
     * 使用提示数组创建
     * 
     * @param objects 提示数组
     */
    public ObjectArgs(Object... objects) {
        this(Arrays.asList(objects));
    }

    /**
     * 空列表
     */
    public ObjectArgs() {
        this(Collections.emptyList());
    }

    @Override
    public List<String> tab(@Nonnull CommandSender sender, @Nonnull String arg) {
        final List<String> argList = new ArrayList<>();
        for (Object object : objects) {
            final String str = object.toString();
            if (str.startsWith(arg)) {
                argList.add(str);
            }
        }
        return argList;
    }
    
}
