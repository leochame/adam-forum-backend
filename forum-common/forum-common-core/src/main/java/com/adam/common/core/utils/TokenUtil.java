package com.adam.common.core.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.adam.common.core.constant.UserConstant;
import com.adam.common.core.model.vo.TokenVO;
import io.jsonwebtoken.SignatureAlgorithm;

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
        TokenVO tokenVO = new TokenVO();
        String accessToken = IdUtil.simpleUUID();
        String encryptToken =  Base64.encode(accessToken + current + userRole);
        tokenVO.setAccessToken(encryptToken);
        tokenVO.setExpireTime(expirationMills);
        return tokenVO;
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

