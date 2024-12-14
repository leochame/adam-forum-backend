package com.adam.common.core.utils;

import cn.hutool.core.util.RandomUtil;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.constant.UserConstant;
import com.adam.common.core.constant.UserRoleEnum;
import com.adam.common.core.exception.ThrowUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 随机头像生成
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/13 23:19
 */
public class RandomAvatarUtil {

    /**
     * 生成随机头像
     *
     * @param userRole 用户身份
     * @return 头像
     **/
    public static String getRandomAvatar(UserRoleEnum userRole) {

        String randomAvatar = null;

        switch (userRole) {
            case USER -> {
                int avatarIndex = RandomUtil.randomInt(UserConstant.DEFAULT_USER_AVATAR_LIST.length);
                randomAvatar = UserConstant.DEFAULT_USER_AVATAR_LIST[avatarIndex];
            }
            case ADMIN -> randomAvatar = UserConstant.DEFAULT_ADMIN_AVATAR;
            case SUPER_ADMIN -> randomAvatar = UserConstant.DEFAULT_SUPER_ADMIN_AVATAR;
        }

        ThrowUtils.throwIf(StringUtils.isBlank(randomAvatar), ErrorCodeEnum.PARAMS_ERROR, "The user role is invalid");
        return randomAvatar;
    }
}
