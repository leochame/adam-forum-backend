package com.adam.common.auth.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.adam.common.auth.config.PathPatterns;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.core.store.UserContext;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

@Component
public class UserAuthorizeFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //1.获取request和response对象
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;


        // 放行注册与登录
        // 如果匹配不需要授权的路径，就不需要校验是否需要授权
        for (String excludePathPattern : PathPatterns.EXCLUDE_PATH_PATTERNS) {
            AntPathMatcher pathMatcher = new AntPathMatcher();
            if (pathMatcher.match(excludePathPattern, req.getRequestURI())) {
                chain.doFilter(req, resp);
                return;
            }
        }

        //3.获取token
        String accessToken = req.getHeader("Authorization");

        // 4.判断token是否存在
        if (StrUtil.isBlank(accessToken)) {
            throw new BusinessException(ErrorCodeEnum.NOT_LOGIN_ERROR);
        }

        // 5. 判断token是否有效
        UserBasicInfoVO userBasicInfoVO = redisCacheService.checkTokenAndGetUserBasicInfo(token);
        if (userBasicInfoVO == null) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "获取 Token 用户信息失败");
        }

        // 存储当前登录用户
        UserContext.setLoginUser(userBasicInfoVO);

        //6.放行
        return chain.filter(exchange);
    }
}
