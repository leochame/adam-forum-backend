package com.adam.post.model.vo;

import com.adam.service.user.bo.UserBasicInfoBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子评论视图类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/25 23:11
 */
@Data
@Schema(name = "帖子评论视图类")
public class PostCommentVO implements Serializable {

    /**
     * id
     */
    @Schema(description = "评论 id")
    private Long id;

    /**
     * 创建用户 id
     */
    @Schema(description = "创建用户信息")
    private UserBasicInfoBO createUser;

    /**
     * 评论内容
     */
    @Schema(description = "评论内容")
    private String content;

    /**
     * 点赞数
     */
    @Schema(description = "点赞数")
    private int thumbNum;

    /**
     * 是否点赞
     */
    @Schema(description = "是否点赞")
    private boolean hasThumb;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 嵌套回复
     */
    @Schema(description = "回复列表")
    private List<ReplyVO> replyList;

    @Data
    @NoArgsConstructor
    public static class ReplyVO {

        /**
         * id
         */
        @Schema(description = "评论 id")
        private Long id;

        /**
         * 创建用户 id
         */
        @Schema(description = "创建用户信息")
        private UserBasicInfoBO createUser;

        /**
         * 回复 id 如果是父comment的id，则不是回复帖，若是其他ReplyComment，则是回复他人帖
         */
        @Schema(description = "回复 id 如果是父comment的id，则不是回复帖，若是其他ReplyComment，则是回复他人帖")
        private Long replyId;

        /**
         * 回复内容
         */
        @Schema(description = "评论内容")
        private String content;

        /**
         * 点赞数
         */
        @Schema(description = "点赞数")
        private int thumbNum;

        /**
         * 是否点赞
         */
        private boolean hasThumb;

        /**
         * 创建时间
         */
        @Schema(description = "创建时间")
        private Date createTime;
    }

    @Serial
    private static final long serialVersionUID = 1846325466174768227L;
}
