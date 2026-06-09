package com.scenic.manager.common;

import lombok.Data;

/**
 * 统一响应结果封装
 * 
 * @param <T> 数据类型
 */
@Data
public class Result<T> {
    /**
     * 响应码：200成功，其他失败
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String msg;
    
    /**
     * 响应数据
     */
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    /**
     * 失败响应（自定义响应码）
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 未授权响应
     */
    public static <T> Result<T> unauthorized(String msg) {
        return new Result<>(401, msg, null);
    }

    /**
     * 无权限响应
     */
    public static <T> Result<T> forbidden(String msg) {
        return new Result<>(403, msg, null);
    }
}
