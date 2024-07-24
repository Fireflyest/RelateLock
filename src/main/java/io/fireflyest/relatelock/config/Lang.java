package io.fireflyest.relatelock.config;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

// import io.fireflyest.emberlib.config.annotation.Entry;
// import io.fireflyest.emberlib.config.annotation.EntryComments;
// import io.fireflyest.emberlib.config.annotation.Yaml;
import io.fireflyest.relatelock.Print;

/**
 * 测试
 * 
 * @author Fireflyest
 * @since 1.0
 */
// @Yaml("lang/de/fault.yml")
public abstract class Lang {
    
    private Lang() {
    }

    // @Entry
    public static final String TEST_STRING = "test";

    // @Entry
    public static final int TEST_INT = 2;

    // @Entry
    // @EntryComments("test")
    public static final int AN_INT = 3;

}
