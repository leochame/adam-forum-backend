package com.adam.user.service.impl;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.cache.constant.CacheConstant;
import com.adam.common.cache.service.RedisCacheService;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.constant.UserConstant;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.service.user.bo.UserBasicInfoBO;
import com.adam.user.mapper.UserMapper;
import com.adam.user.model.entity.User;
import com.adam.user.model.entity.UserFollow;
import com.adam.user.model.enums.FollowEnum;
import com.adam.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.user.service.UserFollowService;
import com.adam.user.mapper.UserFollowMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【user_follow(用户关注表)】的数据库操作Service实现
 * @createDate 2025-01-26 18:04:40
 */
@Service
@Slf4j
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow>
        implements UserFollowService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private RedisCacheService redisCacheService;

    @Override
    public int followUser(Long followedUserId, UserBasicInfoVO currentUser) {
        // 判断关注对象是否存在
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getId, followedUserId)
                .select(User::getUserRole)
                .last(DatabaseConstant.LIMIT_ONE));
        if (user == null || user.getUserRole().equals(UserConstant.BAN_ROLE)) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "该用户无法关注！");
        }

        // 查询关注关系
        UserFollow userFollow = baseMapper.selectOne(Wrappers.<UserFollow>lambdaQuery()
                .eq(UserFollow::getFollowedId, followedUserId)
                .eq(UserFollow::getUserId, currentUser.getId())
                .last(DatabaseConstant.LIMIT_ONE));
        int operation;
        String cacheKey = getFollowingListCacheKey(currentUser.getId());
        // 新增关注关系
        if (userFollow == null) {
            userFollow = new UserFollow();
            userFollow.setFollowedId(followedUserId);
            userFollow.setUserId(currentUser.getId());
            baseMapper.insert(userFollow);
            log.info("用户 「{}」 关注了 用户 「{}」", currentUser.getId(), followedUserId);
            // 缓存中添加
            redisCacheService.addLongValueInSet(cacheKey, followedUserId, CacheConstant.DAY_EXPIRE_TIME, TimeUnit.SECONDS);
            operation = 1;
        } else {
            baseMapper.deleteById(userFollow);
            log.info("用户 「{}」 取消关注了 用户 「{}」", currentUser.getId(), followedUserId);
            // 缓存中删除
            redisCacheService.removeLongValueFromSet(cacheKey, followedUserId);
            operation = -1;
        }
        return operation;
    }

    @Override
    public List<UserBasicInfoBO> listFollow(FollowEnum followEnum) {
        // 获取缓存内容
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        // 从缓存中获取关注列表 id
        String cacheKey = getFollowingListCacheKey(currentUser.getId());
        if (followEnum.equals(FollowEnum.FAN)) {
            cacheKey = getFanListCacheKey(currentUser.getId());
        }
        // 从缓存中获取
        Set<Long> userIdSet = redisCacheService.getLongSet(cacheKey);
        if (CollectionUtils.isEmpty(userIdSet)) {
            String setLimitKey = getSetLimitKey(currentUser.getId(), followEnum);
            long num = redisCacheService.incrLongValue(setLimitKey, 1, CacheConstant.MINUTE_EXPIRE_TIME, TimeUnit.SECONDS);
            if (num >= 6) {
                redisCacheService.addLongValueInSet(
                        cacheKey, CacheConstant.SET_EMPTY_VALUE, CacheConstant.HOUR_EXPIRE_TIME, TimeUnit.SECONDS
                );
                return List.of();
            }

            // 查询数据库
            // 获取关注列表
            if (followEnum.equals(FollowEnum.FOLLOWED)) {
                List<UserFollow> userFollowList = baseMapper.selectList(Wrappers.<UserFollow>lambdaQuery().
                        eq(UserFollow::getUserId, currentUser.getId())
                        .select(UserFollow::getFollowedId));
                userIdSet = userFollowList.stream()
                        .map(UserFollow::getFollowedId)
                        .collect(Collectors.toSet());
            }
            // 获取粉丝列表
            if (followEnum.equals(FollowEnum.FAN)) {
                List<UserFollow> userFollowList = baseMapper.selectList(Wrappers.<UserFollow>lambdaQuery().
                        eq(UserFollow::getFollowedId, currentUser.getId())
                        .select(UserFollow::getUserId));

                userIdSet = userFollowList.stream()
                        .map(UserFollow::getUserId)
                        .collect(Collectors.toSet());
            }

            // 写入缓存
            redisCacheService.addLongSet(cacheKey, userIdSet, CacheConstant.DAY_EXPIRE_TIME, TimeUnit.SECONDS);
        } else if (userIdSet.contains(CacheConstant.SET_EMPTY_VALUE)) {
            // 直接返回空值，防止击穿
            return List.of();
        }

        // 查询数据
        return userService.getUserBasicList(userIdSet);
    }

    private String getFollowingListCacheKey(Long userId) {
        return CacheConstant.USER_PREFIX + CacheConstant.FOLLOWED_USER_ID_LIST_PREFIX + CacheConstant.FOLLOWING_PREFIX + userId;
    }

    private String getFanListCacheKey(Long userId) {
        return CacheConstant.USER_PREFIX + CacheConstant.FOLLOWED_USER_ID_LIST_PREFIX + CacheConstant.FAN_PREFIX + userId;
    }

    private String getSetLimitKey(Long userId, FollowEnum followEnum) {
        if (followEnum.equals(FollowEnum.FAN)) {
            return CacheConstant.USER_PREFIX + CacheConstant.FOLLOWED_USER_ID_LIST_PREFIX + CacheConstant.FAN_PREFIX + CacheConstant.LIMIT + userId;
        }
        if (followEnum.equals(FollowEnum.FOLLOWED)) {
            return CacheConstant.USER_PREFIX + CacheConstant.FOLLOWED_USER_ID_LIST_PREFIX + CacheConstant.FOLLOWING_PREFIX + CacheConstant.LIMIT + userId;
        }
        throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR);
    }

}




