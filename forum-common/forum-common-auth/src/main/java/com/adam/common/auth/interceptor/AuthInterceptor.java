package com.adam.common.auth.interceptor;

import com.adam.common.auth.client.TokenClient;
import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户权限拦截器
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/20 21:54
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private TokenClient tokenClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        log.info("拦截到 {}, 开始校验 token 信息", requestURI);
        // 获取 token 信息
        String accessToken = request.getHeader("Authorization");

        // 判断 token 是否有效
        UserBasicInfoVO userBasicInfoVO = tokenClient.checkTokenAndGetUserBasicInfo(accessToken);
        // 保存上下文
        SecurityContext.setCurrentUser(userBasicInfoVO);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestURI = request.getRequestURI();
        log.info("请求 {} 业务已执行结束", requestURI);
        // 清楚上下文信息，防止上下文内容污染线程
        SecurityContext.clearAll();
    }
}
