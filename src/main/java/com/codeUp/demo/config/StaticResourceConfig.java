package com.codeUp.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapeia /uploads/** para a pasta do seu PC (ajuste o caminho se necessário)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/Users/Andy/Documents/0 - WEB DEV PROJECTS/uploads/");
    }
}
