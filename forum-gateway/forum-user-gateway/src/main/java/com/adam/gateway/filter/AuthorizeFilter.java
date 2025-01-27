package com.adam.gateway.filter;


import com.adam.common.auth.client.TokenClient;
import com.adam.common.auth.constant.JwtConstant;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class AuthorizeFilter implements Ordered, GlobalFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //2.判断是否是登录
        if(request.getURI().getPath().contains("/login")){
            log.info("注册放行");
            //放行
            return chain.filter(exchange);
        }
        //2.判断是否是登录
        if(request.getURI().getPath().contains("/register")){
            //放行
            return chain.filter(exchange);
        }

        log.info("非注册或者登录请求，开始校验登录信息");


        //3.获取token
        String token = request.getHeaders().getFirst("Authorization");

        // 4.判断token是否存在
        if(StringUtils.isBlank(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //5.判断token是否有效
        SecretKey key = Keys.hmacShaKeyFor(JwtConstant.JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        String userId;
        String device;
        Date expireDate;
        try {
            JwtParser jwtParser = Jwts.parser()
                    // 设置签名的秘钥
                    .verifyWith(key)
                    .build();
            Jws<io.jsonwebtoken.Claims> claims = jwtParser.parseSignedClaims(token);
            Claims payload = claims.getPayload();
            userId = payload.get(JwtConstant.CLAIMS_USER_ID, String.class);
            device = payload.get(JwtConstant.CLAIMS_DEVICE, String.class);
            expireDate = payload.getExpiration();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCodeEnum.TOKEN_EXPIRE_ERROR);
        } catch (Exception e) {
            log.error("token 解析错误: {}", e.getMessage());
            throw new BusinessException(ErrorCodeEnum.TOKEN_PARSE_ERROR);
        }

        // 判断 token 是否过期
        if (expireDate.before(new Date())) {
            throw new BusinessException(ErrorCodeEnum.TOKEN_EXPIRE_ERROR);
        }
        //6.放行
        return chain.filter(exchange);
    }

    /**
     * 优先级设置  值越小  优先级越高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}