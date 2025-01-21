package com.adam.service.user.service;

import com.adam.service.user.bo.UserBasicInfoBO;

import java.util.List;

/**
 * 用户基础信息调用 - RPC 接口
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/18 14:47
 */
public interface UserBasicRpcService {

    /**
     * 根据用户 id 获取用户基础信息
     *
     * @param userId 用户 id
     * @return 用户基础信息
     */
    UserBasicInfoBO getUserBasicInfoByUserId(Long userId);

    /**
     * 获取用户基础信息列表
     *
     * @param userIdList 用户 id 列表
     * @return 用户基础信息列表
     */
    List<UserBasicInfoBO> getUserBasicInfoListByUserIdList(List<Long> userIdList);
}
