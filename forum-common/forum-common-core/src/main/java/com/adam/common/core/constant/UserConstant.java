package com.adam.common.core.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 用户相关常量
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/13 23:14
 */
public interface UserConstant {

    /**
     * 用户前缀
     */
    String USER_NAME_PREFIX = "user_";

    /**
     * 普通用户
     */
    String USER_ROLE = "user";

    /**
     * 管理员
     */
    String ADMIN_ROLE = "admin";

    /**
     * 超级管理员
     */
    String SUPER_ADMIN_ROLE = "super_admin";

    /**
     * 普通用户默认头像列表
     */
    String[] DEFAULT_USER_AVATAR_LIST = {
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/12.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/11.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/10.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/13.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/9.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/8.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/7.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/17.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/16.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/15.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/14.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/21.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/20.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/19.png",
            "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/18.png"
    };

    /**
     * 管理员默认头像
     */
    String DEFAULT_ADMIN_AVATAR = "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/7220f2e584d0c6dd243f9c1cb0e41527.png";

    /**
     * 超级管理员头像
     */
    String DEFAULT_SUPER_ADMIN_AVATAR = "https://ice-man-1316749988.cos.ap-shanghai.myqcloud.com/imgs/7220f2e584d0c6dd243f9c1cb0e41527.png";
}
