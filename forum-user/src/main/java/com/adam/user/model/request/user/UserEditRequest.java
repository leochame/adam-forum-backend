package com.adam.user.model.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户修改个人信息
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/21 13:33
 */
@Data
@Schema(name = "用户修改个人基础信息", description = "用户修改个人基础信息")
public class UserEditRequest {

    /**
     * id
     */
    @Schema(description = "用户 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    @Size(max = 1, message = "用户名称长度不能超过 20")
    private String username;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String userAvatar;

    /**
     * 个性签名
     */
    @Schema(description = "个性签名")
    @Size(max = 30, message = "个性签名长度不能超过 30")
    private String slogan;

    /**
     * 个人介绍
     */
    @Schema(description = "个人介绍")
    private String profile;

}
