package com.adam.common.auth.vo;

import com.adam.common.core.model.vo.UserBasicInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * token 缓存 VO
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/20 11:26
 */
@Data
@Schema(name = "token 缓存存储类")
public class TokenCacheVO {

    @Schema(name = "用户登录凭证")
    private String accessToken;

    @Schema(name = "当前登录设备")
    private String device;

    @Schema(name = "用户基础信息")
    private UserBasicInfoVO userBasicInfoVO;
}
