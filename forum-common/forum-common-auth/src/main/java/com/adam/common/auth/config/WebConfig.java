package com.adam.common.auth.config;

import com.adam.common.auth.constant.InterceptorConstant;
import com.adam.common.auth.interceptor.AuthInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/20 21:56
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    /**
     * 设置权限校验拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(InterceptorConstant.AUTH_INTERCEPTOR_PATH_PATTERN)
                .excludePathPatterns(InterceptorConstant.AUTH_INTERCEPTOR_EXCLUDE_PATH_PATTERN)
                .order(0);
    }
}
