package com.adam.user.service.impl;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.constant.UserConstant;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.user.mapper.UserMapper;
import com.adam.user.model.entity.User;
import com.adam.user.model.entity.UserFollow;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.user.service.UserFollowService;
import com.adam.user.mapper.UserFollowMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        // 新增关注关系
        if (userFollow == null) {
            userFollow = new UserFollow();
            userFollow.setFollowedId(followedUserId);
            userFollow.setUserId(currentUser.getId());
            baseMapper.insert(userFollow);
            log.info("用户 「{}」 关注了 用户 「{}」", currentUser.getId(), followedUserId);
            operation = 1;
        } else {
            baseMapper.deleteById(userFollow);
            log.info("用户 「{}」 取消关注了 用户 「{}」", currentUser.getId(), followedUserId);
            operation = -1;
        }

        // todo 删除缓存关注关系

        return operation;
    }
}




