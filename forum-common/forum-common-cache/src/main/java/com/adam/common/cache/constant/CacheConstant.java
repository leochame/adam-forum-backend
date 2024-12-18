package com.adam.common.cache.constant;

/**
 * 缓存相关常量
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/14 21:57
 */
public interface CacheConstant extends AuthCacheConstant, UserCacheConstant, PostCacheConstant {

    /**
     * key内部的连接字符
     */
    String UNION = ":";

    /**
     * 一个月过期时间
     */
    long MONTH_EXPIRE_TIME = 3600 * 24 * 30;

    /**
     * 一周过期时间
     */
    long WEEK_EXPIRE_TIME = 3600 * 24 * 7;

    /**
     * 一天过期时间
     */
    long DAY_EXPIRE_TIME = 3600 * 24;

    /**
     * 一小时过期时间
     */
    long HOUR_EXPIRE_TIME = 3600;

    /**
     * 十分钟过期时间
     */
    long TEN_MINUTE_EXPIRE_TIME = 60 * 10;

    /**
     * 一分钟国旗时间
     */
    long MINUTE_EXPIRE_TIME = 60;
}
