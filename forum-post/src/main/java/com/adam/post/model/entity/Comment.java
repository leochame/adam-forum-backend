package com.adam.post.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/24 19:19
 */
@Data
@Document("post_comment")
public class Comment implements Serializable {

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 帖子 id
     */
    @Indexed
    private Long postId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 嵌套回复
     */
    private List<ReplyComment> replies;

    @Data
    @NoArgsConstructor
    public static class ReplyComment {
        /**
         * id
         */
        @Id
        private Long id;

        /**
         * 创建用户 id
         */
        private Long userId;

        /**
         * 回复 id 如果是父comment的id，则不是回复帖，若是其他ReplyComment，则是回复他人帖
         */
        private Long replyId;

        /**
         * 回复内容
         */
        private String content;

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 是否删除
         */
        private boolean hasDelete;
    }
}
