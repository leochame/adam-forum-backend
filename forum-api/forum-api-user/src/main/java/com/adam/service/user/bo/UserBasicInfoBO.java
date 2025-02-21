package com.adam.service.user.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户基础信息 （RPC 调用）
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/18 14:48
 */
@Data
public class UserBasicInfoBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1851829162451100339L;

    /**
     * id
     */
    @Schema(description = "用户 id")
    private Long id;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String username;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String account;

    /**
     * 性别 0 - 女｜1 - 男
     */
    @Schema(description = "性别")
    private Integer gender;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String userAvatar;

    /**
     * 用户身份
     */
    @Schema(description = "用户身份")
    private String userRole;

    /**
     * 是否关注用户
     */
    @Schema(description = "是否关注用户")
    private Boolean hasFollow;
}
