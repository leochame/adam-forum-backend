package com.adam.user.service;

import com.adam.service.user.bo.UserBasicInfoBO;
import com.adam.user.model.request.user.UserEditRequest;
import com.adam.user.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    /**
     * 用户编辑个人信息
     *
     * @param userEditRequest 用户编辑信息内容
     * @return 编辑成功
     */
    boolean editUser(UserEditRequest userEditRequest);

    /**
     * 获取用户基础信息列表
     *
     * @param userIdList 用户 id
     * @return 用户基础信息列表
     */
    List<UserBasicInfoBO> getUserBasicList(Collection<Long> userIdList);
}
