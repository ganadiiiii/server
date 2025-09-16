package com.ganadi.palmful.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 꽃 이미지 파일 서빙을 위한 정적 리소스 핸들러
        registry.addResourceHandler("/images/flowers/**")
                .addResourceLocations("file:src/main/resources/static/images/flowers/")
                .setCachePeriod(3600); // 1시간 캐시
    }
}
