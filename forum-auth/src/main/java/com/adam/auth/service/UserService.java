package com.adam.auth.service;

import com.adam.auth.model.entity.User;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author chenjiahan
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-12-14 12:04:36
 */
public interface UserService extends IService<User> {

    /**
     * 用户账号密码登录
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @return
     */
    UserBasicInfoVO userLoginByUserAccountAndPassword(String userAccount, String userPassword);
}
