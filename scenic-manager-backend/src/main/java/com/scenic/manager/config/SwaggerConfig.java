package com.scenic.manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI配置类（替代Swagger）
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("景区票务管理系统API文档")
                        .description("景区票务管理系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("scenic-manager")
                                .email("")));
    }
}
