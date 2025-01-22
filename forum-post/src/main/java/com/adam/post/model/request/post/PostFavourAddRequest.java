package com.adam.post.model.request.post;

import java.io.Serial;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 帖子收藏 / 取消收藏请求
 */
@Data
@Schema(name = "帖子点赞请求")
public class PostFavourAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    @Schema(description = "帖子 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long postId;

    @Serial
    private static final long serialVersionUID = 1L;
}