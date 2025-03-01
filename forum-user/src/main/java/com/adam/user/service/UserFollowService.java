package com.adam.user.service;

import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.service.user.bo.UserBasicInfoBO;
import com.adam.user.model.entity.UserFollow;
import com.adam.user.model.enums.FollowEnum;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author chenjiahan
 * @description 针对表【user_follow(用户关注表)】的数据库操作Service
 * @createDate 2025-01-26 18:04:40
 */
public interface UserFollowService extends IService<UserFollow> {

    /**
     * 关注用户
     *
     * @param followedUserId 关注用户 id
     * @param currentUser    当前登录用户
     * @return 返回 1 成功关注，返回 -1 取消关注
     */
    int followUser(Long followedUserId, UserBasicInfoVO currentUser);

    /**
     * 获取关注列表
     *
     * @param followEnum 类型
     * @return 关注列表
     */
    List<UserBasicInfoBO> listFollow(FollowEnum followEnum);
}
