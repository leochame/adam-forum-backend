package com.adam.post.model.request.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 评论点赞请求类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/26 12:28
 */
@Data
@Schema(name = "评论点赞请求类", description = "用户点赞评论")
public class CommentThumbRequest implements Serializable {

    /**
     * 一级评论 id
     */
    @Schema(description = "一级评论 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long firstCommentId;

    /**
     * 二级评论 id
     */
    @Schema(description = "二级评论 id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long secondCommentId;

    @Serial
    private static final long serialVersionUID = -1193580211066426527L;
}
