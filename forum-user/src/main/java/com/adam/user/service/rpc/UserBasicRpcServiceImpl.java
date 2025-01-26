package com.adam.user.service.rpc;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.BusinessRpcException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.service.user.bo.UserBasicInfoBO;
import com.adam.service.user.service.UserBasicRpcService;
import com.adam.user.mapper.UserMapper;
import com.adam.user.model.entity.User;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

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

    @Override
    public UserBasicInfoBO getUserBasicInfoByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessRpcException(ErrorCodeEnum.PARAMS_ERROR, "用户 id 错误");
        }

        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getId, userId)
                .last(DatabaseConstant.LIMIT_ONE));

        ThrowUtils.throwRpcIf(user == null, ErrorCodeEnum.NOT_FOUND_ERROR, "用户信息不存在");
        UserBasicInfoBO userBasicInfoBO = new UserBasicInfoBO();
        BeanUtils.copyProperties(user, userBasicInfoBO);

        return userBasicInfoBO;
    }

    @Override
    public List<UserBasicInfoBO> getUserBasicInfoListByUserIdList(Set<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            throw new BusinessRpcException(ErrorCodeEnum.PARAMS_ERROR, "用户 id 列表错误");
        }

        List<User> userList = userMapper.selectList(Wrappers.<User>lambdaQuery()
                .in(User::getId, userIdList));
        return userList.stream().map(user -> {
            UserBasicInfoBO userBasicInfoBO = new UserBasicInfoBO();
            BeanUtils.copyProperties(user, userBasicInfoBO);
            return userBasicInfoBO;
        }).toList();
    }
}
