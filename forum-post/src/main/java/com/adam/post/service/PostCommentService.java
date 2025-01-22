package com.adam.post.service;

import com.adam.post.model.entity.PostComment;
import com.adam.post.model.request.comment.CommentAddRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author chenjiahan
 * @description 针对表【post_comment(帖子评论表)】的数据库操作Service
 * @createDate 2025-01-22 13:34:50
 */
public interface PostCommentService extends IService<PostComment> {

    /**
     * 发布帖子评论
     *
     * @param commentAddRequest 帖子评论请求
     * @return 评论 id
     */
    Long addComment(CommentAddRequest commentAddRequest);
}
