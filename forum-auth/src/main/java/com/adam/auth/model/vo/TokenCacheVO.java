package com.adam.auth.model.vo;

import com.adam.common.core.model.vo.UserBasicInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "token 缓存存储类", description = "缓存中存储 token 类")
public class TokenCacheVO {

    @Schema(description = "token信息")
    private String accessToken;

    @Schema(description = "用户基础信息")
    private UserBasicInfoVO userInfo;

    @Schema(description = "当前登录设备")
    private String device;
}
