package com.adam.common.auth.security;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.model.vo.UserBasicInfoVO;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程保存信息
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/20 11:11
 */
public class SecurityContext {

    private static final String CURRENT_LOGIN_USER = "LOGIN_USER";

    private static final ThreadLocal<Map<String, Object>> requestContext = ThreadLocal.withInitial(HashMap::new);

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户信息
     */
    public static UserBasicInfoVO getCurrentUser() {
        return (UserBasicInfoVO) requestContext.get().get(CURRENT_LOGIN_USER);
    }

    /**
     * 设置当前登录用户信息
     *
     * @param user 当前登录用户
     */
    public static void setCurrentUser(UserBasicInfoVO user) {
        requestContext.get().put(CURRENT_LOGIN_USER, user);
    }

    /**
     * 移除当前登录用户
     */
    public static void removeCurrentUser() {
        requestContext.get().remove(CURRENT_LOGIN_USER);
    }

    /**
     * 清楚所有字段
     */
    public static void clearAll() {
        requestContext.get().clear();
    }

}
