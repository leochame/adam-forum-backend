package com.adam.post.repository;

import com.adam.post.model.entity.CommentThumb;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 评论点赞 mongodb 查询类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/26 12:40
 */
public interface CommentThumbRepository extends MongoRepository<CommentThumb, Long> {

    /**
     * 根据 id 获取评论点赞信息
     *
     * @param commentId 评论 id
     * @return 点赞信息
     */
    CommentThumb getCommentThumbByCommentId(Long commentId);

}
