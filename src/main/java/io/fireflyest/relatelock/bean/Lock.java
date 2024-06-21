package io.fireflyest.relatelock.bean;

public class Lock {
    
    // 玩家uuid
    private String owner;

    // 共享玩家
    private String friends;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

}
