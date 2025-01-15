package com.adam.common.cache.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/14 22:20
 */
public interface RedisCacheService {

    /**
     * 设置 String 类型缓存 默认过期时间(默认过期时间 1 天)
     *
     * @param key   缓存键
     * @param value 缓存 String 值
     */
    void setValue(String key, String value);

    /**
     * 设置 String 类型缓存 指定过期时间
     *
     * @param key        缓存键
     * @param value      缓存 String 值
     * @param expireTime 过期时间
     * @param timeUnit   时间单位
     */
    void setValueWithExpireTime(String key, String value, long expireTime, TimeUnit timeUnit);

    /**
     * 设置 String 类型缓存 指定过期时间
     *
     * @param key        缓存键
     * @param value      缓存 String 值
     * @param expireTime 过期时间(秒)
     */
    void setValueWithExpireTime(String key, String value, long expireTime);

    /**
     * 设置 long 类型缓存 制定过期时间
     *
     * @param key        缓存键
     * @param value      缓存 String 值
     * @param expireTime 过期时间(秒)
     */
    void setLongValueWithExpireTime(String key, long value, long expireTime);

    /**
     * 设置 对象 类型缓存 默认过期时间(默认过期时间 1 天)
     *
     * @param key   缓存键
     * @param value 缓存 String 值
     */
    void setObject(String key, Object value);

    /**
     * 设置 对象 类型缓存 指定过期时间
     *
     * @param key        缓存键
     * @param value      缓存 对象 值
     * @param expireTime 过期时间
     * @param timeUnit   时间单位
     */
    void setObjectWithExpireTime(String key, Object value, long expireTime, TimeUnit timeUnit);

    /**
     * 设置 对象 类型缓存 指定过期时间
     *
     * @param key        缓存键
     * @param value      缓存 对象 值
     * @param expireTime 过期时间(秒)
     */
    void setObjectWithExpireTime(String key, Object value, long expireTime);


    /**
     * 获取 String 类型的缓存值
     *
     * @param key 缓存键
     * @return String 类型的缓存值
     */
    String getValue(String key);

    /**
     * 获取 long 类型的缓存值
     *
     * @param key 缓存键
     * @return String 类型的缓存值
     */
    long getLongValue(String key);

    /**
     * 增加 long 缓存值
     *
     * @param key   缓存键
     * @param delta 递增值
     * @return 缓存值
     */
    long incrLongValue(String key, long delta);

    /**
     * 减少 long 缓存值
     *
     * @param key   缓存键
     * @param delta 递减值
     * @return 缓存值
     */
    long decrLongValue(String key, long delta);

    /**
     * 是否拥有键
     *
     * @param key 缓存键
     * @return 是否拥有键
     */
    boolean hasKey(String key);

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    void removeKey(String key);

    /**
     * 获取 对象 类型的缓存值
     *
     * @param key        缓存键
     * @param valueClass 获取对象的Class
     * @param <T>        对象类型
     * @return 缓存对象
     */
    <T> T getObject(String key, Class<T> valueClass);

    /**
     * 获取 对象列表 类型的缓存值
     *
     * @param key        缓存键
     * @param valueClass 获取对象的Class
     * @param <T>        对象类型
     * @return 缓存对象列表
     */
    <T> List<T> getObjectList(String key, Class<T> valueClass);

}
