package io.fireflyest.relatelock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 后台打印
 * @author Fireflyest
 * @since 1.0
 */
public enum Print {

    RELATE_LOCK("RelateLock");

    private static final String COLOR_RESET = "\033[0m";
    private static final String COLOR_BLACK = "\033[30m";
    private static final String COLOR_RED = "\033[31m";
    private static final String COLOR_GREEN = "\033[32m";
    private static final String COLOR_YELLOW = "\033[33m";
    private static final String COLOR_BLUE = "\033[34m";
    private static final String COLOR_PURPLE = "\033[35m";
    private static final String COLOR_CYAN = "\033[36m";

    private static final String BACKGROUND_RED = "\033[41m";

    private final String title;
    private final Logger logger;

    private boolean debug;

    private Print(String title) {
        this.title = title;
        this.logger = LogManager.getLogger(title);
    }

    public void catching(Throwable throwable) {
        // TODO: 
        logger.catching(throwable);
    }

    public void debug(String message, Object... params) {
        if (debug) {
            message = "[" + COLOR_BLUE + title + COLOR_RESET + "] " + message;
            logger.info(message, params);
        }
    }

    public void info(String message, Object... params) {
        message = "[" + COLOR_GREEN + title + COLOR_RESET + "] " + message;
        logger.info(message, params);
    }

    public void warn(String message, Object... params) {
        message = "[" + COLOR_YELLOW + title + COLOR_RESET + "] " + message;
        logger.warn(message, params);
    }

    public void error(String message, Object... params) {
        message = "[" + COLOR_RED + title + COLOR_RESET + "] " + message;
        logger.error(message, params);
    }

    public void fatal(String message, Object... params) {
        message = BACKGROUND_RED + "[" + title + "] " + COLOR_RESET + message;
        logger.fatal(message, params);
    }

    public void onDebug() {
        this.debug = true;
    }

    public void offDebug() {
        this.debug = false;
    }

}
