package com.adam.post.model.request.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 删除评论请求
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/25 20:59
 */
@Data
@Schema(name = "删除评论请求", description = "用户删除一级评论、二级评论请求。如果只有 firstCommentId 则删除整个一级评论，如果包含 secondCommentId，删除一级评论下对饮的二级评论")
public class CommentDeleteRequest implements Serializable {

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
    private static final long serialVersionUID = -4172909855594514561L;
}
