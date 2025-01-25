package com.adam.post.service.impl;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.generator.SnowflakeIdGenerator;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.post.mapper.PostMapper;
import com.adam.post.model.entity.Post;
import com.adam.post.model.mongodb.Comment;
import com.adam.post.model.request.comment.CommentAddRequest;
import com.adam.post.repository.CommentRepository;
import com.adam.post.service.PostCommentService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author chenjiahan
 * @description 针对表【post_comment(帖子评论表)】的数据库操作Service实现
 * @createDate 2025-01-22 13:34:50
 */
@Slf4j
@Service
public class PostCommentServiceImpl implements PostCommentService {

    private static final SnowflakeIdGenerator SNOWFLAKE_ID_GENERATOR = SnowflakeIdGenerator.getInstance();
    ;

    @Resource
    private PostMapper postMapper;

    @Resource
    private CommentRepository commentRepository;

    @Override
    public Long addComment(CommentAddRequest commentAddRequest) {
        String content = commentAddRequest.getContent();
        ThrowUtils.throwIf(content == null, ErrorCodeEnum.PARAMS_ERROR, "评论内容不能为空");
        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();

        // 判断帖子是否存在
        Post post = postMapper.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, commentAddRequest.getPostId())
                .select(Post::getId, Post::getCommentNum)
                .last(DatabaseConstant.LIMIT_ONE));
        ThrowUtils.throwIf(post == null, ErrorCodeEnum.NOT_FOUND_ERROR, "原帖不存在");

        // 判断是评论还是回复帖
        Long commentId = commentAddRequest.getCommentId();
        long returnId;

        if (commentId == null) {
            // 评论帖
            Comment comment = new Comment();
            comment.setId(SNOWFLAKE_ID_GENERATOR.nextId());
            comment.setPostId(commentAddRequest.getPostId());
            comment.setUserId(currentUser.getId());
            comment.setContent(content);
            comment.setThumbNum(0);
            comment.setCreateTime(new Date());
            comment.setReplies(Collections.emptyList());
            commentRepository.save(comment);
            returnId = comment.getId();
        } else {
            // 回复帖
            Long replyId = commentAddRequest.getReplyId();
            Comment firstComment = commentRepository.getCommentById(commentId);
            ThrowUtils.throwIf(firstComment == null, ErrorCodeEnum.NOT_FOUND_ERROR, "回复评论不存在！");
            List<Comment.ReplyComment> replyList = firstComment.getReplies();
            if (replyId != null) {
                // 判断回复帖是否存在
                Optional<Comment.ReplyComment> replyCommentOptional = replyList.stream()
                        .filter(replyComment -> replyComment.getId().equals(replyId))
                        .findFirst();
                ThrowUtils.throwIf(replyCommentOptional.isEmpty(), ErrorCodeEnum.NOT_FOUND_ERROR, "回复评论已被删除！");
            }
            Comment.ReplyComment replyComment = new Comment.ReplyComment();
            replyComment.setId(SNOWFLAKE_ID_GENERATOR.nextId());
            replyComment.setUserId(currentUser.getId());
            replyComment.setReplyId(replyId);
            replyComment.setContent(content);
            replyComment.setCreateTime(new Date());
            replyComment.setHasDelete(false);
            returnId = replyComment.getId();
            replyList.add(replyComment);

            // 更新数据
            firstComment.setReplies(replyList);
            commentRepository.save(firstComment);
        }

        // 更新帖子回复数 + 1
        postMapper.update(Wrappers.<Post>lambdaUpdate()
                .eq(Post::getId, post.getId())
                .set(Post::getCommentNum, post.getCommentNum() + 1));
        log.info("用户 {} 新增评论 「{}」，帖子 id：{}", currentUser.getId(), commentAddRequest.getContent(), commentAddRequest.getPostId());
        return returnId;
    }

    @Override
    public int deleteComment(Long firstCommentId, Long secondCommentId) {
        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        Long userId = currentUser.getId();

        // 获取一级评论
        Comment comment = commentRepository.getCommentById(firstCommentId);
        ThrowUtils.throwIf(comment == null, ErrorCodeEnum.NOT_FOUND_ERROR, "一级评论不存在，请重试！");

        // 删除回复数
        int removeNum = 1;

        List<Comment.ReplyComment> replyList = comment.getReplies();
        if (secondCommentId != null) {
            // 删除二级评论
            Comment.ReplyComment reply = replyList.stream()
                    .filter(replyComment -> replyComment.getId().equals(secondCommentId))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "评论不存在，请重试！"));
            reply.setHasDelete(true);
            comment.setReplies(replyList);
            commentRepository.save(comment);
        } else {
            // 删除一级评论
            ThrowUtils.throwIf(!comment.getUserId().equals(userId), ErrorCodeEnum.NO_AUTH_ERROR, "仅评论创作者可删除！");
            if (!replyList.isEmpty()) {
                removeNum = (int) replyList.stream()
                        .filter(replyComment -> !replyComment.isHasDelete())
                        .count() + 1;
            }
            commentRepository.deleteById(firstCommentId);
        }

        // 更新帖子回复数
        postMapper.update(Wrappers.<Post>lambdaUpdate()
                .eq(Post::getId, comment.getPostId())
                .setSql("comment_num = comment_num - " + removeNum));
        log.info("用户 {} 删除评论id：{}，帖子：{}，总共删除：{}", userId, comment.getId(), comment.getPostId(), removeNum);
        return removeNum;
    }
}




