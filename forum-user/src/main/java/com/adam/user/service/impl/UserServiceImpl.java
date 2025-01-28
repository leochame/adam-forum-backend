package com.adam.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.constant.UserConstant;
import com.adam.common.core.enums.UserRoleEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.core.utils.RandomAvatarUtil;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.service.user.bo.UserBasicInfoBO;
import com.adam.user.model.request.user.UserEditRequest;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.user.model.entity.User;
import com.adam.user.service.UserService;
import com.adam.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author iceman
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-12-12 21:19:05
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public long userRegisterByAccount(String userAccount, String userPassword, String checkPassword) {
        // 校验参数
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "账号密码不能为空！");
        }
        if (userPassword.length() < 8 || userAccount.length() > 22) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "密码长度不得小于8位，大于22位");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "确认密码必须和密码相同");
        }
        synchronized (userAccount.intern()) {
            // 账号不能重复
            boolean userExist = baseMapper.exists(Wrappers.<User>lambdaQuery()
                    .eq(User::getAccount, userAccount));
            ThrowUtils.throwIf(userExist, ErrorCodeEnum.OPERATION_ERROR, "用户账号已存在！");
            // todo 优化加密方式
            String encryptPassword = DigestUtils.md5DigestAsHex((userPassword).getBytes());
            String userName = UserConstant.USER_NAME_PREFIX + RandomUtil.randomString(8);
            String randomUserAvatar = RandomAvatarUtil.getRandomAvatar(UserRoleEnum.USER);
            User user = new User();
            user.setUsername(userName);
            user.setAccount(userAccount);
            user.setPassword(encryptPassword);
            user.setUserAvatar(randomUserAvatar);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "注册失败，请重试!");
            }
            return user.getId();
        }
    }

    @Override
    public boolean editUser(UserEditRequest userEditRequest) {
        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();

        // 1. 校验参数
        if (!currentUser.getId().equals(userEditRequest.getUserId())) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "仅能编辑自己的个人信息！");
        }

        // 判断用户是否存在
        User user = baseMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getId, userEditRequest.getUserId())
                .select(User::getId)
                .last(DatabaseConstant.LIMIT_ONE));
        ThrowUtils.throwIf(ObjectUtils.isEmpty(user), ErrorCodeEnum.NOT_FOUND_ERROR, "编辑用户数据不存在");

        BeanUtils.copyProperties(userEditRequest, user);
        boolean result = baseMapper.updateById(user) != 0;
        ThrowUtils.throwIf(!result, ErrorCodeEnum.OPERATION_ERROR, "编辑自己信息失败，请重试！");
        log.info("成功更新用户信息 {}", userEditRequest);
        return true;
    }

    @Override
    public List<UserBasicInfoBO> getUserBasicList(Collection<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return List.of();
        }
        List<User> userList = baseMapper.selectList(Wrappers.<User>lambdaQuery()
                .in(User::getId, userIdList)
                .select(User::getId, User::getUsername, User::getGender, User::getUserAvatar, User::getUserRole));
        return userList.stream().map(user -> {
            UserBasicInfoBO userBasicInfoBO = new UserBasicInfoBO();
            BeanUtils.copyProperties(user, userBasicInfoBO);
            return userBasicInfoBO;
        }).toList();
    }
}




