package com.adam.user.model.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
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
    @Schema(description = "用户账户", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 6, message = "账号长度不得小于 6 位")
    @Size(max = 10, message = "账号长度不得超过 10 位")
    private String userAccount;

    /**
     * 用户密码
     */
    @Schema(description = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 8, message = "密码长度不得小于 8 位")
    @Size(max = 22, message = "密码长度不得超过 22 位")
    private String userPassword;

    /**
     * 确认密码
     */
    @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String checkPassword;

    @Serial
    private static final long serialVersionUID = 1883918332008926351L;
}
