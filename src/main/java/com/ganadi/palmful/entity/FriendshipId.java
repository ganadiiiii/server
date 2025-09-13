package com.ganadi.palmful.entity;

import java.io.Serializable;
import java.util.Objects;

public class FriendshipId implements Serializable {
    
    private Long user;
    private Long friend;
    
    // Constructors
    public FriendshipId() {}
    
    public FriendshipId(Long user, Long friend) {
        this.user = user;
        this.friend = friend;
    }
    
    // Getters and Setters
    public Long getUser() {
        return user;
    }
    
    public void setUser(Long user) {
        this.user = user;
    }
    
    public Long getFriend() {
        return friend;
    }
    
    public void setFriend(Long friend) {
        this.friend = friend;
    }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipId that = (FriendshipId) o;
        return Objects.equals(user, that.user) && Objects.equals(friend, that.friend);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(user, friend);
    }
}
