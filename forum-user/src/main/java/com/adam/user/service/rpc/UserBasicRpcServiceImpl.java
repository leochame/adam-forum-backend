package com.adam.user.service.rpc;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.BusinessRpcException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.service.user.bo.UserBasicInfoBO;
import com.adam.service.user.service.UserBasicRpcService;
import com.adam.user.mapper.UserFollowMapper;
import com.adam.user.mapper.UserMapper;
import com.adam.user.model.entity.User;
import com.adam.user.model.entity.UserFollow;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户基础信息调用 - RPC 实现
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/18 14:51
 */
@DubboService
public class UserBasicRpcServiceImpl implements UserBasicRpcService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserFollowMapper userFollowMapper;

    @Override
    public UserBasicInfoBO getUserBasicInfoByUserId(Long userId, UserBasicInfoVO currentUser) {
        if (userId == null || userId <= 0) {
            throw new BusinessRpcException(ErrorCodeEnum.PARAMS_ERROR, "用户 id 错误");
        }

        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getId, userId)
                .last(DatabaseConstant.LIMIT_ONE));

        ThrowUtils.throwRpcIf(user == null, ErrorCodeEnum.NOT_FOUND_ERROR, "用户信息不存在");
        UserBasicInfoBO userBasicInfoBO = new UserBasicInfoBO();
        BeanUtils.copyProperties(user, userBasicInfoBO);

        // 判断用户是否关注
        boolean hasFollow = userFollowMapper.exists(Wrappers.<UserFollow>lambdaQuery()
                .eq(UserFollow::getFollowedId, currentUser.getId())
                .eq(UserFollow::getUserId, user.getId()));
        userBasicInfoBO.setHasFollow(hasFollow);

        return userBasicInfoBO;
    }

    @Override
    public List<UserBasicInfoBO> getUserBasicInfoListByUserIdList(Set<Long> userIdList, UserBasicInfoVO currentUser) {
        if (CollectionUtils.isEmpty(userIdList)) {
            throw new BusinessRpcException(ErrorCodeEnum.PARAMS_ERROR, "用户 id 列表错误");
        }

        List<User> userList = userMapper.selectList(Wrappers.<User>lambdaQuery()
                .in(User::getId, userIdList));

        // 判断用户是否关注
        List<UserFollow> userFollowList = userFollowMapper.selectList(Wrappers.<UserFollow>lambdaQuery()
                .eq(UserFollow::getUserId, currentUser.getId())
                .in(UserFollow::getUserId, userIdList));
        Set<Long> followUserId = userFollowList.stream().map(UserFollow::getUserId).collect(Collectors.toSet());
        return userList.stream().map(user -> {
            UserBasicInfoBO userBasicInfoBO = new UserBasicInfoBO();
            BeanUtils.copyProperties(user, userBasicInfoBO);
            userBasicInfoBO.setHasFollow(followUserId.contains(user.getId()));
            return userBasicInfoBO;
        }).toList();
    }
}
