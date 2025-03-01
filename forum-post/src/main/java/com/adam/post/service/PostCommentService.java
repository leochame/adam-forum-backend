package com.adam.post.service;

import com.adam.post.model.entity.Comment;
import com.adam.post.model.request.comment.CommentAddRequest;
import com.adam.post.model.request.comment.CommentQueryRequest;
import com.adam.post.model.vo.PostCommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author chenjiahan
 * @description 针对表【post_comment(帖子评论表)】的数据库操作Service
 * @createDate 2025-01-22 13:34:50
 */
public interface PostCommentService {

    /**
     * 发布帖子评论
     *
     * @param commentAddRequest 帖子评论请求
     * @return 评论 id
     */
    Long addComment(CommentAddRequest commentAddRequest);

    /**
     * 删除评论
     *
     * @param firstCommentId  一级评论 id
     * @param secondCommentId 二级评论 id
     * @return 删除成功
     */
    int deleteComment(Long firstCommentId, Long secondCommentId);

    /**
     * 根据 id 获取评论信息
     *
     * @param commentId 评论 id
     * @return 评论信息
     */
    PostCommentVO getCommentVOById(Long commentId);

    /**
     * 分页获取评论信息
     *
     * @param commentQueryRequest 查询参数
     * @return 评论信息分页
     */
    Page<PostCommentVO> pageCommentVO(CommentQueryRequest commentQueryRequest);

    /**
     * 评论点赞
     *
     * @param firstCommentId  一级评论 id
     * @param secondCommentId 二级评论 id
     * @return 点赞状态
     */
    int thumbComment(Long firstCommentId, Long secondCommentId);
}
