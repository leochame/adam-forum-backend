package com.adam.common.core.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 登录 Token 凭证VO
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/15 11:36
 */
@Data
@Schema(name = "登录用户 Token 信息")
public class TokenVO {

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * 过期时间
     */
    private Long expireTime;

}
