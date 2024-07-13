package io.fireflyest.relatelock.bean;

import java.util.HashSet;
import java.util.Set;

/**
 * 锁
 * @author Fireflyest
 * @since 1.0
 */
public class Lock {
    
    // 拥有者uuid
    private String owner;

    // 共享玩家uuid
    private Set<String> share;

    // 日志
    private Set<String> log;
    
    // 新建日期
    private long outset;
    
    // 锁类型
    private String type;
    
    // 锁相关数据，例如密码
    private String data;

    /**
     * 无参构造器，用于json生成obj
     */
    public Lock() {
        //
    }

    /**
     * 构建新的锁
     * @param owner 所有者uuid
     * @param outset 起始时间
     * @param type 锁类型
     * @param data 锁数据
     */
    public Lock(String owner, long outset, String type, String data) {
        this.owner = owner;
        this.outset = outset;
        this.type = type;
        this.data = data;
        this.share = new HashSet<>();
        this.log = new HashSet<>();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Set<String> getShare() {
        return share;
    }

    public void setShare(Set<String> share) {
        this.share = share;
    }

    public Set<String> getLog() {
        return log;
    }

    public void setLog(Set<String> log) {
        this.log = log;
    }

    public long getOutset() {
        return outset;
    }

    public void setOutset(long outset) {
        this.outset = outset;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
