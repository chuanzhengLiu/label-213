package com.scenic.manager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 景区票务管理系统启动类
 * 
 * @author scenic-manager
 */
@SpringBootApplication
@MapperScan("com.scenic.manager.mapper")
public class ScenicManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScenicManagerApplication.class, args);
    }
}
