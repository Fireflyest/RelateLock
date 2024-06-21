package io.fireflyest.relatelock;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import io.fireflyest.relatelock.listener.LockEventListener;

public final class RelateLock extends JavaPlugin {
    
    private final Logger logger = LogManager.getLogger(this.getClass());

    public static RelateLock getPlugin() {
        return getPlugin(RelateLock.class);
    }

    @Override
    public void onEnable() {
        Print.RELATE_LOCK.onDebug();
        logger.warn("ee {}hello world!{} aa", "\033[32;1m", "\033[0m");
        try {
            for (int i = 0; i < 64; i++) {
                for (int j = 0; j < 64; j++) {
                    Thread.sleep(1);
                    logger.info(color("test {}", i), i);
                }
                throw new IOException();
            }
        } catch (InterruptedException | IOException e) {
            Print.RELATE_LOCK.catching(e);
            Print.RELATE_LOCK.info("------");
            for (StackTraceElement stackTrace : e.getStackTrace()) {
                Print.RELATE_LOCK.debug(stackTrace.toString() + " --- ");
            }
        }
        
        // this.test();

        this.getServer().getPluginManager().registerEvents(new LockEventListener(), this);
    }

    private String color(String text, int color1) {
        return String.format("\033[%dm", color1) + text + "\033[0m";
    }

    public static void test() {
        try {
            for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
                final String className = stackTrace.getClassName();
                final Class<?> aClass = Class.forName(className);
                if (JavaPlugin.class.isAssignableFrom(aClass)) {
                    System.out.println(stackTrace.toString());
                    break;
                }
            }
            throw new IOException();
        } catch (Exception e) {
            
            Print.RELATE_LOCK.catching(e);
            for (StackTraceElement stackTrace : e.getStackTrace()) {
                System.out.println(stackTrace.toString());
            }
        }
        
    }

    @Override
    public void onDisable() {
        // close data service
        
    }
}
