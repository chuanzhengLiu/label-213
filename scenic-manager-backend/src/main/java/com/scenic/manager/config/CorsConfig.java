package com.scenic.manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {
    
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;
    
    @Value("${cors.allowed-methods}")
    private String allowedMethods;
    
    @Value("${cors.allowed-headers}")
    private String allowedHeaders;
    
    @Value("${cors.allow-credentials}")
    private Boolean allowCredentials;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的源
        config.addAllowedOrigin(allowedOrigins);
        
        // 允许的HTTP方法
        String[] methods = allowedMethods.split(",");
        for (String method : methods) {
            config.addAllowedMethod(method.trim());
        }
        
        // 允许的请求头
        config.addAllowedHeader(allowedHeaders);
        
        // 允许携带凭证
        config.setAllowCredentials(allowCredentials);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
