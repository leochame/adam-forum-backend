package com.adam.common.cache.service.impl;

import com.adam.common.cache.constant.CacheConstant;
import com.adam.common.cache.service.RedisCacheService;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/14 22:28
 */
@Service
@Slf4j
public class RedisCacheServiceImpl implements RedisCacheService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final Gson GSON = new Gson();


    @Override
    public void setValue(String key, String value) {
        setValueWithExpireTime(key, value, CacheConstant.DAY_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    @Override
    public void setValueWithExpireTime(String key, String value, long expireTime) {
        setValueWithExpireTime(key, value, expireTime, TimeUnit.SECONDS);
    }

    @Override
    public void setLongValueWithExpireTime(String key, long value, long expireTime) {
        setValueWithExpireTime(key, String.valueOf(value), expireTime, TimeUnit.SECONDS);
    }

    @Override
    public void setObject(String key, Object value) {
        setValueWithExpireTime(key, GSON.toJson(value), CacheConstant.DAY_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    @Override
    public void setObjectWithExpireTime(String key, Object value, long expireTime, TimeUnit timeUnit) {
        setValueWithExpireTime(key, GSON.toJson(value), expireTime, timeUnit);
    }

    @Override
    public void setObjectWithExpireTime(String key, Object value, long expireTime) {
        setValueWithExpireTime(key, GSON.toJson(value), expireTime, TimeUnit.SECONDS);
    }

    @Override
    public void setValueWithExpireTime(String key, String value, long expireTime, TimeUnit timeUnit) {
        if (StringUtils.isAnyBlank(key)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_KEY_EMPTY_ERROR);
        }
        if (StringUtils.isAnyBlank(value)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_VALUE_EMPTY_ERROR);
        }
        try {
            stringRedisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
            log.info("Successfully save Redis cache, key: {}, value: {}, expireTime: {} ({})", key, value, expireTime, timeUnit.name());
        } catch (Exception e) {
            log.error("Redis set key: {} value error: {}", key, e.getMessage());
        }
    }

    @Override
    public String getValue(String key) {
        if (StringUtils.isAnyBlank(key)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_KEY_EMPTY_ERROR);
        }
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get key: {} value error: {}", key, e.getMessage());
        }
        return StringUtils.EMPTY;
    }

    @Override
    public long getLongValue(String key) {
        String value = getValue(key);
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            log.error("Redis get long key: {} value error: {}", key, e.getMessage());
        }
        return 0L;
    }

    @Override
    public long incrLongValue(String key, long delta, long expireTime, TimeUnit timeUnit) {
        if (StringUtils.isEmpty(key)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_KEY_EMPTY_ERROR);
        }
        if (delta <= 0) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "递增值必须大于0");
        }
        try {
            Long value = stringRedisTemplate.opsForValue().increment(key, delta);
            stringRedisTemplate.expire(key, expireTime, timeUnit);
            return (value != null) ? value : 0L;
        } catch (Exception e) {
            log.error("Redis incr key: {}, delta: {} value error: {}", key, delta, e.getMessage());
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "递增缓存值装失败");
        }
    }

    @Override
    public long decrLongValue(String key, long delta) {
        if (StringUtils.isEmpty(key)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_KEY_EMPTY_ERROR);
        }
        if (delta <= 0) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "递增值必须大于0");
        }
        try {
            Long value = stringRedisTemplate.opsForValue().decrement(key, delta);
            return (value != null) ? value : 0L;
        } catch (Exception e) {
            log.error("Redis decr key: {}, delta: {} value error: {}", key, delta, e.getMessage());
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "递增缓存值装失败");
        }
    }

    @Override
    public boolean hasKey(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_KEY_EMPTY_ERROR);
        }
        try {
            return Optional.ofNullable(stringRedisTemplate.hasKey(key)).orElse(false);
        } catch (Exception e) {
            log.error("Error detect Redis has Key: {}", e.getMessage());
            return Boolean.FALSE;
        }
    }

    @Override
    public void removeKey(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_KEY_EMPTY_ERROR);
        }
        try {
            stringRedisTemplate.delete(key);
            log.info("Successfully remove Redis cache, key: {}", key);
        } catch (Exception e) {
            log.error("Error delete Redis Key: {}", e.getMessage());
        }
    }

    @Override
    public <T> T getObject(String key, Class<T> valueClass) {
        if (StringUtils.isEmpty(key)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_KEY_EMPTY_ERROR);
        }
        String value = getValue(key);
        try {
            return GSON.fromJson(value, valueClass);
        } catch (Exception e) {
            log.error("Redis get Object key: {} value error: {}", key, e.getMessage());
        }
        return null;
    }

    @Override
    public <T> List<T> getObjectList(String key, Class<T> valueClass) {
        if (StringUtils.isEmpty(key)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_KEY_EMPTY_ERROR);
        }
        String value = getValue(key);
        try {
            return GSON.fromJson(value, new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception e) {
            log.error("Redis get Object List key: {} value error: {}", key, e.getMessage());
        }
        return null;
    }

    @Override
    public void addLongValueInSet(String key, Long value, Long expireTime, TimeUnit timeUnit) {
        this.addLongSet(key, Collections.singleton(value), expireTime, timeUnit);
    }

    @Override
    public void addLongSet(String key, Set<Long> values, Long expireTime, TimeUnit timeUnit) {
        if (StringUtils.isEmpty(key)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_KEY_EMPTY_ERROR);
        }
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        try {
            // 判断是否为 空值
            stringRedisTemplate.opsForSet().remove(key, String.valueOf(CacheConstant.SET_EMPTY_VALUE));
            stringRedisTemplate.opsForSet().add(key,
                    values.stream().map(String::valueOf).distinct().toArray(String[]::new));
            stringRedisTemplate.expire(key, expireTime, timeUnit);
            log.info("Successfully add {} to {} set, expireTime: {} ({})", GSON.toJson(values), key, expireTime, timeUnit.name());
        } catch (Exception e) {
            log.error("Redis add value {} to {} set error: {}", GSON.toJson(values), key, e.getMessage());
        }
    }

    @Override
    public Set<Long> getLongSet(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_KEY_EMPTY_ERROR);
        }
        try {
            Set<String> stringSet = stringRedisTemplate.opsForSet().members(key);
            // 如果集合为空或不存在，直接返回 null
            if (CollectionUtils.isEmpty(stringSet)) {
                return null;
            }
            // 转换成 Long Set
            return stringSet.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Redis get Set key: {} value error: {}", key, e.getMessage());
        }
        return null;
    }

    @Override
    public void removeLongValueFromSet(String key, Long value) {
        if (StringUtils.isEmpty(key)) {
            throw new BusinessException(ErrorCodeEnum.CACHE_KEY_EMPTY_ERROR);
        }
        try {
            stringRedisTemplate.opsForSet().remove(key, value.toString());
            log.info("Successfully remove {} value from set {}", value, key);
        } catch (Exception e) {
            log.error("Error remove {} Redis Key, error: {}", key, e.getMessage());
        }
    }
}

