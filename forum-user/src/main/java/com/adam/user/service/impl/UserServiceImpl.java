package com.adam.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.constant.UserConstant;
import com.adam.common.core.enums.UserRoleEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.utils.RandomAvatarUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.user.model.entity.User;
import com.adam.user.service.UserService;
import com.adam.user.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author iceman
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-12-12 21:19:05
 */
@Service
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
}




