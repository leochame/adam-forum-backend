package com.adam.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户账号密码登录请求类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/15 11:47
 */
@Data
@Schema(name = "用户账号密码登录请求", description = "用户采用账号，密码进行登录")
public class UserPasswordLoginRequest implements Serializable {

    /**
     * 用户账号
     */
    @Schema(description = "用户账户", requiredMode = Schema.RequiredMode.REQUIRED)
    public String userAccount;

    /**
     * 用户密码
     */
    @Schema(description = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED)
    public String userPassword;

    @Serial
    private static final long serialVersionUID = 2147620140317487302L;
}
