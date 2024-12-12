package com.adam.fliter;

import io.micrometer.common.util.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 权限校验
 */
@Component
public class AuthorizeFilter implements Ordered, GlobalFilter {
    /**
     *优先级设置
     */
    @Override
    public int getOrder() {
        return 0;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 放行注册与登录
        if(request.getURI().getPath().contains("/login")){
            return chain.filter(exchange);
        }

        if(request.getURI().getPath().contains("/register")){
            return chain.filter(exchange);
        }

        //3.获取token
        String token = request.getHeaders().getFirst("token");

        // 4.判断token是否存在
        if(StringUtils.isBlank(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //TODO 5.判断token是否有效

        //6.放行
        return chain.filter(exchange);
    }
}
