package com.adam.common.auth.constant;

/**
 * JWT 令牌相关常量
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/20 11:34
 */
public interface JwtConstant {

    /**
     * jwt 密钥（一定要满足 38 位长度）
     */
    String JWT_SECRET_KEY = "adam-forum_is_the_best_project_liu&ice";

    /**
     * 用户 id
     */
    String CLAIMS_USER_ID = "USER_ID";

    /**
     * 当前登录设备
     */
    String CLAIMS_DEVICE = "DEVICE";
}
