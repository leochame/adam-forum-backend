package com.adam.common.cache.constant;

/**
 * 用户缓存相关常量
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/14 22:14
 */
public interface UserCacheConstant {
    /**
     * 用户 相关key
     */
    String USER_PREFIX = "forum_user:";

    /**
     * 关注列表 相关key
     */
    String FOLLOWED_USER_ID_LIST_PREFIX = "follow_user_id_list:";

    /**
     * 关注 key
     */
    String FOLLOWING_PREFIX = "following:";

    /**
     * 粉丝 key
     */
    String FAN_PREFIX = "fan:";
}
