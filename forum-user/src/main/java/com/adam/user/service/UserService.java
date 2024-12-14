package com.adam.user.service;

import com.adam.user.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author iceman
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-12-12 21:19:05
 */
public interface UserService extends IService<User> {

    /**
     * 用户账号注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 用户唯一id
     */
    long userRegisterByAccount(String userAccount, String userPassword, String checkPassword);
}
