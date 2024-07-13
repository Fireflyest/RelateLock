package io.fireflyest.relatelock.config;

/**
 * 配置
 * 
 * @author Fireflyest
 * @since 1.0 
 */
public record Config(
    boolean debug, 
    String lockString, 
    String lockPasswordString, 
    String lockFeeString, 
    String lockTokenString, 
    String shareSymbol, 
    String managerSymbol,
    String removeSymbol) {}
