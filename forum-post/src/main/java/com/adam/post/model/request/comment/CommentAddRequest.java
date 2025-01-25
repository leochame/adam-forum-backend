package com.adam.post.model.request.comment;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 帖子评论表
 *
 * @TableName post_comment
 */
@Data
@Schema(name = "发布评论请求", description = "用户发布对帖子评论、回复其他评论")
public class CommentAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    @Schema(description = "帖子 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long postId;

    /**
     * 一级评论 id，为 Null 则为一级评论
     */
    private Long commentId;

    /**
     * 回复 id，支持嵌套评论，顶级评论为 null
     */
    @Schema(description = "回复帖子 id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long replyId;

    /**
     * 评论内容
     */
    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 200, message = "内容不能超过200字！")
    private String content;

    @Serial
    private static final long serialVersionUID = 1L;
}