package com.adam.common.cache.constant;

/**
 * 授权缓存相关常量
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/14 21:57
 */
public interface AuthCacheConstant {

    /**
     * auth 相关key
     */
    String AUTH_PREFIX = "forum_auth:";

    /**
     * token 授权相关key
     */
    String OAUTH_TOKEN_PREFIX = AUTH_PREFIX + "token:";

    /**
     * 保存token 缓存使用key
     */
    String ACCESS = OAUTH_TOKEN_PREFIX + "access:";

    /**
     * 刷新token 缓存使用key
     */
    String REFRESH_TO_ACCESS = OAUTH_TOKEN_PREFIX + "refresh_to_access:";
}
