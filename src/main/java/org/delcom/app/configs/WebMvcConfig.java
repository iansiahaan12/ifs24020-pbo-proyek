package org.delcom.app.configs;

import org.delcom.app.interceptors.AuthInterceptor;
import org.delcom.app.interceptors.SessionInterceptor; // Import baru
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private SessionInterceptor sessionInterceptor; // Inject baru

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. Interceptor untuk API (JWT Base)
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**")
                .excludePathPatterns("/api/public/**");

        // 2. Interceptor untuk Web/HTML (Session Base) - BARU
        // Dijalankan untuk semua URL kecuali API dan Static files
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**") // Semua path
                .excludePathPatterns("/api/**", "/css/**", "/js/**", "/images/**");
    }
}