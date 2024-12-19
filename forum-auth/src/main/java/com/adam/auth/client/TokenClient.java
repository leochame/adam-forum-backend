package com.adam.auth.client;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.adam.auth.model.vo.TokenCacheVO;
import com.adam.common.cache.constant.CacheConstant;
import com.adam.common.cache.service.RedisCacheService;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.constant.UserConstant;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.TokenVO;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class TokenClient {

    @Resource
    private RedisCacheService redisCacheService;

    private static final String SECRET_KEY = "adam-forum_is_the_best_project";

    /**
     * 生成 accessToken
     *
     * @param userBasicInfoVO 用户 基础信息
     * @param device          登录设备
     * @return accessToken
     */
    public TokenVO createTokenVOAndStore(UserBasicInfoVO userBasicInfoVO, String device) {
        long current = System.currentTimeMillis();
        String userRole = userBasicInfoVO.getUserRole();
        long expirationMills = current + getExpireTime(userRole);
        TokenVO tokenVO = new TokenVO();
        String accessToken = IdUtil.simpleUUID();
        String encryptToken = Base64.encode(accessToken + current + userRole);
        tokenVO.setAccessToken(encryptToken);
        tokenVO.setExpireTime(expirationMills);

        // 存储 token 缓存信息
        String cacheKey = getCacheKey(encryptToken, device);
        TokenCacheVO tokenCacheVO = new TokenCacheVO();
        tokenCacheVO.setAccessToken(encryptToken);
        tokenCacheVO.setDevice(device);


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
     */
    public void storeToken(String key, TokenCacheVO tokenCacheVO) {
        // 判断是当前设备是否已经登录
        if (redisCacheService.hasKey(key)) {
            // 把上一个 token 删除，踢下线
            redisCacheService.removeKey(key);
            log.info("");
        }

        // 存储 token
//        redisCacheService.setObjectWithExpireTime(key, userBasicInfoVO, period);
    }

    /**
     * 校验 token 并获取用户基础信息
     *
     * @param token accessToken
     * @return 用户基础信息
     */
    public UserBasicInfoVO checkTokenAndGetUserBasicInfo(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(ErrorCodeEnum.NOT_LOGIN_ERROR, "用户未登录");
        }

        String key = CacheConstant.ACCESS + token;

        // 判断 Redis 是否过期
        if (!redisCacheService.hasKey(key)) {
            throw new BusinessException(ErrorCodeEnum.NO_TOKEN_ERROR, "用户登录状态过期，请重新登录");
        }

        // 返回用户基础信息
        return redisCacheService.getObject(key, UserBasicInfoVO.class);
    }

    /**
     * 生成存储在缓存中的 key（Base64加密）
     *
     * @param accessToken token
     * @param device      设备信息
     * @return 缓存中的 key
     */
    private String getCacheKey(String accessToken, String device) {
        String key = accessToken + CacheConstant.UNION + device;
        return Base64.encode(key);
    }

}
