package com.adam.auth.service.impl;

import com.adam.auth.mapper.UserMapper;
import com.adam.auth.model.entity.User;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.constant.UserConstant;
import com.adam.common.core.constant.UserRoleEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.database.constant.DatabaseConstant;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author chenjiahan
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-12-14 12:04:36
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public UserBasicInfoVO userLoginByUserAccountAndPassword(String userAccount, String userPassword) {
        // 1. 校验参数
        if (StringUtils.isBlank(userAccount)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "账号为空");
        }
        if (StringUtils.isBlank(userPassword)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "密码为空");
        }

        User user = baseMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getAccount, userAccount)
                .last(DatabaseConstant.LIMIT_ONE));
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "用户不存在，请检查账号是否正确！");
        }
        // 判断密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword).getBytes());
        if (!encryptPassword.equals(user.getPassword())) {
            throw new BusinessException(ErrorCodeEnum.USER_PASSWORD_ERROR);
        }
        // 判断用户是否被 ban
        if (UserConstant.BAN_ROLE.equals(user.getUserRole())) {
            throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR, "账号被禁用，请联系管理员");
        }

        // 填充基础信息
        UserBasicInfoVO userBasicInfoVO = new UserBasicInfoVO();
        userBasicInfoVO.setId(user.getId());
        userBasicInfoVO.setUsername(user.getUsername());
        userBasicInfoVO.setUserAvatar(user.getUserAvatar());
        userBasicInfoVO.setPhone(user.getPhone());
        userBasicInfoVO.setEmail(user.getEmail());
        userBasicInfoVO.setUserRole(user.getUserRole());
        return userBasicInfoVO;
    }
}




