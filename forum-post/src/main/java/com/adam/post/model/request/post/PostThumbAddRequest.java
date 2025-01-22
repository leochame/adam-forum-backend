package com.adam.post.model.request.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子点赞请求
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/22 09:38
 */
@Data
@Schema(name = "帖子点赞请求")
public class PostThumbAddRequest implements Serializable {


    /**
     * 帖子 id
     */
    @Schema(description = "帖子 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long postId;

    @Serial
    private static final long serialVersionUID = 3522452346708173791L;
}
