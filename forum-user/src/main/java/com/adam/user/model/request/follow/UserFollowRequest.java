package com.adam.user.model.request.follow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户关注 / 取消关注请求
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/26 18:08
 */
@Data
public class UserFollowRequest implements Serializable {

    /**
     * 关注用户 id
     */
    @Schema(description = "关注用户 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long followedUserId;

    @Serial
    private static final long serialVersionUID = 4384396454009922956L;
}
