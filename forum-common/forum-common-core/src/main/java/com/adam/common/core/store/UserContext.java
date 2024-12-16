package com.adam.common.core.store;

import com.adam.common.core.model.vo.UserBasicInfoVO;

/**
 * 线程存储当前登录用户
 */
public class UserContext {
    /**
     * The request holder.
     */
    private static final ThreadLocal<UserBasicInfoVO> LOGIN_USER_BASIC_INFO = new ThreadLocal<>();

    /**
     * 获取登录用户信息
     *
     * @return 登录用户基础信息
     */
    public static UserBasicInfoVO getLoginUser() {
        return LOGIN_USER_BASIC_INFO.get();
    }

    /**
     * 设置登录用户信息
     *
     * @param userInfoInTokenBo 当前登录用户
     */
    public static void setLoginUser(UserBasicInfoVO userInfoInTokenBo) {
        LOGIN_USER_BASIC_INFO.set(userInfoInTokenBo);
    }

    /**
     * 清除当前登录用户
     */
    public static void clean() {
        if (LOGIN_USER_BASIC_INFO.get() != null) {
            LOGIN_USER_BASIC_INFO.remove();
        }
    }

}
