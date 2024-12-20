package com.adam.common.auth.client;

import cn.hutool.core.codec.Base64;
import com.adam.common.auth.constant.JwtConstant;
import com.adam.common.cache.constant.CacheConstant;
import com.adam.common.cache.service.RedisCacheService;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.constant.LogColorConstant;
import com.adam.common.core.constant.UserConstant;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.auth.vo.TokenCacheVO;
import com.adam.common.auth.vo.TokenVO;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/20 11:13
 */
@Component
@Slf4j
public class TokenClient {

    @Resource
    private RedisCacheService redisCacheService;

    /**
     * 生成 token 并存储在缓存中
     *
     * @param userBasicInfoVO 用户基础信息
     * @param device          当前登录设备
     * @return tokenVO
     */
    public TokenVO createTokenVOAndStore(UserBasicInfoVO userBasicInfoVO, String device) {
        long current = System.currentTimeMillis();
        String userRole = userBasicInfoVO.getUserRole();
        long expireCacheTime = getExpireTime(userRole);
        Date date = new Date(current + expireCacheTime);
        Long userId = userBasicInfoVO.getId();

        // 生成 jwt token
        SecretKey key = Keys.hmacShaKeyFor(JwtConstant.JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        Map<String, String> claims = new HashMap<>();
        claims.put(JwtConstant.CLAIMS_USER_ID, String.valueOf(userId));
        claims.put(JwtConstant.CLAIMS_DEVICE, device);

        JwtBuilder builder = Jwts.builder()
                .signWith(key)
                .claims(claims)
                .expiration(date);
        String accessToken = builder.compact();

        TokenVO tokenVO = new TokenVO();
        tokenVO.setAccessToken(accessToken);
        tokenVO.setExpireTime(current + expireCacheTime);

        // 存储 token 缓存信息
        String cacheKey = CacheConstant.ACCESS + getCacheKey(String.valueOf(userId), device);
        TokenCacheVO tokenCacheVO = new TokenCacheVO();
        tokenCacheVO.setAccessToken(accessToken);
        tokenCacheVO.setDevice(device);
        tokenCacheVO.setUserBasicInfoVO(userBasicInfoVO);
        storeToken(cacheKey, tokenCacheVO, expireCacheTime);

        return tokenVO;
    }


    /**
     * 获取过期时间
     *
     * @param userRole 用户身份
     * @return 过期时间
     */
    private long getExpireTime(String userRole) {
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

    /**
     * 存储 AccessToken
     *
     * @param key          缓存键
     * @param tokenCacheVO 缓存存储用户凭证信息
     * @param expireTime   过期时间
     */
    public void storeToken(String key, TokenCacheVO tokenCacheVO, long expireTime) {
        // 判断是当前设备是否已经登录
        if (redisCacheService.hasKey(key)) {
            // 把上一个 token 删除，踢下线
            redisCacheService.removeKey(key);
            log.info(LogColorConstant.BLUE + "用户 {} 在 {} 设备已踢下线", key, tokenCacheVO.getDevice());
        }

        // 存储 token
        redisCacheService.setObjectWithExpireTime(key, tokenCacheVO, expireTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 校验 token 并获取用户基础信息
     *
     * @param accessToken accessToken
     * @return 用户基础信息
     */
    public UserBasicInfoVO checkTokenAndGetUserBasicInfo(String accessToken) {
        if (StringUtils.isEmpty(accessToken)) {
            throw new BusinessException(ErrorCodeEnum.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 解析 token
        SecretKey key = Keys.hmacShaKeyFor(JwtConstant.JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        String userId;
        String device;
        Date expireDate;
        try {
            JwtParser jwtParser = Jwts.parser()
                    // 设置签名的秘钥
                    .verifyWith(key)
                    .build();
            Jws<Claims> claims = jwtParser.parseSignedClaims(accessToken);
            Claims payload = claims.getPayload();
            userId = payload.get(JwtConstant.CLAIMS_USER_ID, String.class);
            device = payload.get(JwtConstant.CLAIMS_DEVICE, String.class);
            expireDate = payload.getExpiration();
        } catch (Exception e) {
            log.error("token 解析错误: {}", e.getMessage());
            throw new BusinessException(ErrorCodeEnum.TOKEN_PARSE_ERROR);
        }

        // 判断 token 是否过期
        if (expireDate.before(new Date())) {
            throw new BusinessException(ErrorCodeEnum.TOKEN_EXPIRE_ERROR);
        }

        // 获取缓存键值
        String cacheKey = CacheConstant.ACCESS + getCacheKey(userId, device);

        // 判断 Redis 是否过期
        if (!redisCacheService.hasKey(cacheKey)) {
            throw new BusinessException(ErrorCodeEnum.TOKEN_EXPIRE_ERROR, "用户登录状态过期，请重新登录");
        }
        // 取出缓存中 token 信息
        TokenCacheVO tokenCacheVO = redisCacheService.getObject(cacheKey, TokenCacheVO.class);

        // 返回用户基础信息
        return tokenCacheVO.getUserBasicInfoVO();
    }

    /**
     * 移除当前用户 token 缓存
     *
     * @param userBasicInfoVO 用户基础信息
     * @param device          当前登录设备
     */
    public void removeTokenCache(UserBasicInfoVO userBasicInfoVO, String device) {
        String cacheKey = CacheConstant.ACCESS + getCacheKey(String.valueOf(userBasicInfoVO.getId()), device);
        redisCacheService.removeKey(cacheKey);
        log.info("当前用户名: {}, id: {}, 退出 {} 端登录",
                userBasicInfoVO.getUsername(), userBasicInfoVO.getId(), device);
    }

    /**
     * 生成存储在缓存中的 key（Base64加密）
     *
     * @param userId 用户id
     * @param device 设备信息
     * @return 缓存中的 key
     */
    private String getCacheKey(String userId, String device) {
        String key = userId + CacheConstant.UNION + device;
        return Base64.encode(key);
    }


}
