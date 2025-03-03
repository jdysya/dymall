package com.hmall.common.utils;

import java.util.List;

public class UserContext {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();
    private static final ThreadLocal<String> tlRole = new ThreadLocal<>();

    /**
     * 保存当前登录用户信息到ThreadLocal
     * @param userId 用户id
     */
    public static void setUser(Long userId) {
        tl.set(userId);
    }

    public static void setRole(String role) {
        tlRole.set(role);
    }
    /**
     * 获取当前登录用户信息
     * @return 用户id
     */
    public static Long getUser() {
        return tl.get();
    }

    public static String getRole() {
        return tlRole.get();
    }

    /**
     * 移除当前登录用户信息
     */
    public static void removeUser(){
        tl.remove();
    }
    public static void removeRole(){
        tlRole.remove();
    }

}
