package com.adam.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户账号密码注册请求体
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/13 22:46
 */
@Data
@Schema(name = "用户账号密码注册请求", description = "用户采用账号，密码进行简单注册")
public class UserAccountRegisterRequest implements Serializable {

    /**
     * 用户账号
     */
    @Schema(description = "用户账户")
    private String userAccount;

    /**
     * 用户密码
     */
    @Schema(description = "用户密码")
    private String userPassword;

    /**
     * 确认密码
     */
    @Schema(description = "确认密码")
    private String checkPassword;

    private static final long serialVersionUID = 1883918332008926351L;
}
