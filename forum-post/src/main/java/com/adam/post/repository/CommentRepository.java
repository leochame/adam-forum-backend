package com.adam.post.repository;

import com.adam.post.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 评论 mongodb 查询类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/25 19:27
 */
public interface CommentRepository extends MongoRepository<Comment, Long> {

    /**
     * 根据 postId 删除评论列表
     *
     * @param postId 帖子 id
     */
    void deleteByPostId(Long postId);

    /**
     * 根据 id 获取评论
     *
     * @param id 评论 id
     * @return 评论
     */
    Comment getCommentById(@NonNull Long id);
}
