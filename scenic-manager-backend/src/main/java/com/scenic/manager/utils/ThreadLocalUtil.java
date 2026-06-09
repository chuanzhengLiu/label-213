package com.scenic.manager.utils;

import lombok.Data;

/**
 * 线程本地变量工具类，用于存储当前请求用户信息
 */
public class ThreadLocalUtil {
    
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    /**
     * 设置当前用户信息
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色
     */
    public static void setUser(Long userId, String username, Integer role) {
        UserContext context = new UserContext();
        context.setUserId(userId);
        context.setUsername(username);
        context.setRole(role);
        userContext.set(context);
    }

    /**
     * 获取当前用户ID
     * 
     * @return 用户ID
     */
    public static Long getUserId() {
        UserContext context = userContext.get();
        return context != null ? context.getUserId() : null;
    }

    /**
     * 获取当前用户名
     * 
     * @return 用户名
     */
    public static String getUsername() {
        UserContext context = userContext.get();
        return context != null ? context.getUsername() : null;
    }

    /**
     * 获取当前用户角色
     * 
     * @return 角色
     */
    public static Integer getRole() {
        UserContext context = userContext.get();
        return context != null ? context.getRole() : null;
    }

    /**
     * 清除当前用户信息
     */
    public static void clear() {
        userContext.remove();
    }

    /**
     * 用户上下文信息
     */
    @Data
    private static class UserContext {
        private Long userId;
        private String username;
        private Integer role;
    }
}
