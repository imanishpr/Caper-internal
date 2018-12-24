package com.khelacademy.www.pojos;

import java.util.List;

public class DrawHelper {
    private String pool;
    private String categoryPlusUnderX;
    private List<User> user;

    public String getCategotyPlusUnderX() {
        return categoryPlusUnderX;
    }

    public void setCategotyPlusUnderX(String categotyPlusUnderX) {
        this.categoryPlusUnderX = categotyPlusUnderX;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }
}
