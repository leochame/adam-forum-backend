package com.adam.post.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Set;

/**
 * 评论点赞
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/26 12:21
 */
@Data
@Document("post_comment_thumb")
public class CommentThumb implements Serializable {

    /**
     * 评论 id
     */
    @Id
    @Indexed(unique = true)
    private Long commentId;

    /**
     * 点赞用户 id 列表
     */
    private Set<Long> userIdList;
}
