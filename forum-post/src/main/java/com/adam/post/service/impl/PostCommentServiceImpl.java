package com.adam.post.service.impl;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.post.mapper.PostCommentMapper;
import com.adam.post.mapper.PostMapper;
import com.adam.post.model.entity.Post;
import com.adam.post.model.entity.PostComment;
import com.adam.post.model.request.comment.CommentAddRequest;
import com.adam.post.service.PostCommentService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author chenjiahan
 * @description 针对表【post_comment(帖子评论表)】的数据库操作Service实现
 * @createDate 2025-01-22 13:34:50
 */
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
        implements PostCommentService {

    @Resource
    private PostMapper postMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addComment(CommentAddRequest commentAddRequest) {
        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();

        // 判断帖子是否存在
        Post post = postMapper.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, commentAddRequest.getPostId())
                .select(Post::getCommentNum)
                .last(DatabaseConstant.LIMIT_ONE));
        ThrowUtils.throwIf(post == null, ErrorCodeEnum.NOT_FOUND_ERROR, "原帖不存在");

        // 判断回复帖是否存在
        if (commentAddRequest.getParentId() != null) {
            PostComment postComment = baseMapper.selectOne(Wrappers.<PostComment>lambdaQuery()
                    .eq(PostComment::getId, commentAddRequest.getParentId())
                    .select(PostComment::getReplyNum)
                    .last(DatabaseConstant.LIMIT_ONE));
            ThrowUtils.throwIf(postComment == null, ErrorCodeEnum.NOT_FOUND_ERROR, "回复帖子已不存在！");
            // 更新父帖子回复数
            baseMapper.update(Wrappers.<PostComment>lambdaUpdate()
                    .eq(PostComment::getId, commentAddRequest.getParentId())
                    .set(PostComment::getReplyNum, postComment.getReplyNum() + 1));
        }

        PostComment postComment = new PostComment();
        BeanUtils.copyProperties(commentAddRequest, postComment);
        postComment.setUserId(currentUser.getId());
        baseMapper.insert(postComment);

        // 更新 post 回复数
        postMapper.update(Wrappers.<Post>lambdaUpdate()
                .eq(Post::getId, commentAddRequest.getPostId())
                .set(Post::getCommentNum, post.getCommentNum() + 1));

        return postComment.getId();
    }
}




