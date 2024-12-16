package com.adam.common.core.utils;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.constant.UserConstant;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.TokenVO;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Objects;

/**
 * 登录 token 工具
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/15 14:16
 */
public class TokenUtil {

    private static final String SECRET_KEY = "adam-forum_is_the_best_project";
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    /**
     * 生成 accessToken
     *
     * @param userId   用户 id
     * @param userRole 用户角色
     * @return accessToken
     */
    public static TokenVO createTokenVO(Long userId, String userRole) {
        long current = System.currentTimeMillis();
        long expirationMills = current + getExpireTime(userRole);
        byte[] secretKeyBytes = SECRET_KEY.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, SIGNATURE_ALGORITHM.getJcaName());
        DefaultClaims claims = new DefaultClaims();
        claims.setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(current))
                .setExpiration(new Date(expirationMills));
        claims.put("userRole", userRole);
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .signWith(SIGNATURE_ALGORITHM, secretKeySpec)
                .compact();
        TokenVO tokenVO = new TokenVO();
        tokenVO.setAccessToken(accessToken);
        tokenVO.setExpireTime(expirationMills);
        return tokenVO;
    }


    /**
     * 解析 token 用户信息
     *
     * @param accessToken 登录凭证
     * @return 用户基础信息
     */
    public static UserBasicInfoVO checkToken(String accessToken) {
        byte[] secretKeyBytes = SECRET_KEY.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, SIGNATURE_ALGORITHM.getJcaName());
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKeySpec)
                    .parseClaimsJws(accessToken)
                    .getBody();
            String userIdStr = claims.getSubject();
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR, "Token失效");
            }
        } catch (ExpiredJwtException e) {
//            throw new BusinessException()
        }
        return null;
    }

    /**
     * 获取过期时间
     *
     * @param userRole 用户身份
     * @return 过期时间
     */
    public static long getExpireTime(String userRole) {
        // 一个月过期时间
        long expireTimeMills = 3600 * 24 * 30 * 1000L;

        // 普通用户token过期时间 一个月
        if (Objects.equals(userRole, UserConstant.USER_ROLE)) {
            return expireTimeMills;
        }
        // 系统管理员的token过期时间 2 小时
        if (Objects.equals(userRole, UserConstant.SUPER_ADMIN_ROLE) || Objects.equals(userRole, UserConstant.ADMIN_ROLE)) {
            expireTimeMills = expireTimeMills * 2;
        }
        return expireTimeMills;
    }


}

