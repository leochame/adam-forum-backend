package com.adam.post.model.request.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子表
 *
 * @TableName post
 */
@Data
@Schema(name = "帖子更新请求", description = "用户更新帖子")
public class PostEditRequest implements Serializable {
    /**
     * id
     */
    @Schema(description = "帖子 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 标题
     */
    @Schema(description = "帖子标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 30, message = "帖子标题长度不能超过 30 个字")
    private String title;

    /**
     * 内容
     */
    @Schema(description = "帖子内容", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 2000, message = "帖子内容长度不能超过 2000 个字")
    private String content;

    /**
     * 发布地点
     */
    @Schema(description = "帖子发布地点", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 50, message = "发布地点信息不能超过 50 个字")
    private String address;

    @Serial
    private static final long serialVersionUID = 1L;
}