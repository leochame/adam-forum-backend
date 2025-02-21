package com.adam.common.core.model.vo;

import com.adam.common.core.enums.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/15 11:51
 */
@Data
@Schema(name = "用户基础信息VO", description = "后端传递基础用户信息")
public class UserBasicInfoVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户身份
     *
     * @see UserRoleEnum
     */
    private String userRole;

    @Serial
    private static final long serialVersionUID = -4155475112918047935L;
}
