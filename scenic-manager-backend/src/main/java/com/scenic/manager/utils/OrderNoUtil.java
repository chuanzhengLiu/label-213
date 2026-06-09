package com.scenic.manager.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单编号生成工具类
 * 规则：yyyyMMddHHmmss + 6位随机数
 */
public class OrderNoUtil {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    /**
     * 生成订单编号
     * 
     * @return 订单编号
     */
    public static String generateOrderNo() {
        // 获取当前时间戳（yyyyMMddHHmmss格式）
        String timestamp = LocalDateTime.now().format(FORMATTER);
        
        // 生成6位随机数
        int randomNum = 100000 + RANDOM.nextInt(900000);
        
        return timestamp + randomNum;
    }
}
