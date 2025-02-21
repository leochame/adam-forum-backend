package com.adam.post.service.impl;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.generator.SnowflakeIdGenerator;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.post.mapper.PostMapper;
import com.adam.post.model.entity.Comment;
import com.adam.post.model.entity.CommentThumb;
import com.adam.post.model.entity.Post;
import com.adam.post.model.request.comment.CommentAddRequest;
import com.adam.post.model.request.comment.CommentQueryRequest;
import com.adam.post.model.vo.PostCommentVO;
import com.adam.post.repository.CommentRepository;
import com.adam.post.repository.CommentThumbRepository;
import com.adam.post.service.PostCommentService;
import com.adam.service.user.bo.UserBasicInfoBO;
import com.adam.service.user.service.UserBasicRpcService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【post_comment(帖子评论表)】的数据库操作Service实现
 * @createDate 2025-01-22 13:34:50
 */
@Slf4j
@Service
public class PostCommentServiceImpl implements PostCommentService {

    private static final SnowflakeIdGenerator SNOWFLAKE_ID_GENERATOR = SnowflakeIdGenerator.getInstance();

    @Resource
    private PostMapper postMapper;

    @Resource
    private CommentRepository commentRepository;

    @Resource
    private CommentThumbRepository commentThumbRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @DubboReference
    private UserBasicRpcService userBasicRpcService;

    @Override
    public Long addComment(CommentAddRequest commentAddRequest) {
        String content = commentAddRequest.getContent();
        String image = commentAddRequest.getImage();
        ThrowUtils.throwIf(StringUtils.isAllBlank(content, image), ErrorCodeEnum.PARAMS_ERROR, "评论不能为空");
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
            comment.setImage(image);
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
            replyComment.setImage(image);
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
        long removeCommentThumbId = firstCommentId;

        List<Comment.ReplyComment> replyList = comment.getReplies();
        if (secondCommentId != null) {
            // 删除二级评论
            Comment.ReplyComment reply = replyList.stream()
                    .filter(replyComment -> replyComment.getId().equals(secondCommentId))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "评论不存在，请重试！"));
            reply.setHasDelete(true);
            comment.setReplies(replyList);
            removeCommentThumbId = secondCommentId;
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
        // 删除点赞信息
        commentThumbRepository.deleteById(removeCommentThumbId);
        return removeNum;
    }

    @Override
    public PostCommentVO getCommentVOById(Long commentId) {
        // 当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        Comment comment = commentRepository.getCommentById(commentId);
        ThrowUtils.throwIf(comment == null, ErrorCodeEnum.NOT_FOUND_ERROR, "获取评论信息不存在！");
        List<Comment.ReplyComment> replyList = comment.getReplies();

        Set<Long> userIdSet = new HashSet<>();
        Set<Long> commentIdSet = new HashSet<>();
        // 获取所有二级评论用户 id
        replyList.forEach(reply -> {
            userIdSet.add(reply.getUserId());
            commentIdSet.add(reply.getId());
        });
        // 添加一级评论用户id
        userIdSet.add(comment.getUserId());
        commentIdSet.add(comment.getId());
        // 获取所有用户信息
        List<UserBasicInfoBO> createUserList = userBasicRpcService.getUserBasicInfoListByUserIdList(userIdSet, currentUser);
        // userId -> createUser
        Map<Long, UserBasicInfoBO> createUserMap = createUserList.stream()
                .collect(Collectors.toMap(UserBasicInfoBO::getId, user -> user));

        // 获取点赞信息
        Query query = new Query();
        query.addCriteria(Criteria.where("commentId").in(commentIdSet));
        List<CommentThumb> commentThumbs = mongoTemplate.find(query, CommentThumb.class);
        // 当前用户点在过的评论集合
        Set<Long> hasThumbCommentSet = commentThumbs.stream()
                .filter(commentThumb -> commentThumb.getUserIdList().contains(currentUser.getId()))
                .map(CommentThumb::getCommentId).collect(Collectors.toSet());
        // commentId -> comment.thumbNum
        Map<Long, Integer> thumbNumMap = commentThumbs.stream()
                .collect(Collectors.toMap(
                        CommentThumb::getCommentId,
                        commentThumb -> commentThumb.getUserIdList().size())
                );

        return this.CommentToVO(comment, createUserMap, hasThumbCommentSet, thumbNumMap);
    }

    @Override
    public Page<PostCommentVO> pageCommentVO(CommentQueryRequest commentQueryRequest) {
        // 获取登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        Long userId = currentUser.getId();

        Long postId = commentQueryRequest.getPostId();
        // 这里 pageable 页数从 0 开始，所以要 -1
        int current = (int) commentQueryRequest.getCurrent() - 1;
        int pageSize = (int) commentQueryRequest.getPageSize();
        Pageable pageable = PageRequest.of(current, pageSize);
        Query query = new Query();
        if (postId != null) {
            query.addCriteria(Criteria.where("postId").is(postId));
        }
        query.with(pageable);
        long total = mongoTemplate.count(query, Comment.class);
        // 查询评论信息
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);
        if (CollectionUtils.isEmpty(commentList)) {
            return new Page<>(current, pageSize, total);
        }

        Set<Long> userIdSet = new HashSet<>();
        Set<Long> commentIdSet = new HashSet<>();
        commentList.forEach(comment -> {
            userIdSet.add(comment.getUserId());
            commentIdSet.add(comment.getId());
            comment.getReplies().forEach(replyComment -> {
                commentIdSet.add(replyComment.getId());
                userIdSet.add(replyComment.getUserId());
            });
        });
        List<UserBasicInfoBO> createUserList = userBasicRpcService.getUserBasicInfoListByUserIdList(userIdSet, currentUser);
        // userId -> createUser
        Map<Long, UserBasicInfoBO> createUserMap = createUserList.stream()
                .collect(Collectors.toMap(UserBasicInfoBO::getId, user -> user));

        // 获取点赞信息
        query = new Query();
        query.addCriteria(Criteria.where("commentId").in(commentIdSet));
        List<CommentThumb> commentThumbs = mongoTemplate.find(query, CommentThumb.class);
        // 当前用户点在过的评论集合
        Set<Long> hasThumbCommentSet = commentThumbs.stream()
                .filter(commentThumb -> commentThumb.getUserIdList().contains(userId))
                .map(CommentThumb::getCommentId).collect(Collectors.toSet());
        // commentId -> comment.thumbNum
        Map<Long, Integer> thumbNumMap = commentThumbs.stream()
                .collect(Collectors.toMap(
                        CommentThumb::getCommentId,
                        commentThumb -> commentThumb.getUserIdList().size())
                );

        List<PostCommentVO> commentVOList = commentList.stream()
                .map(comment -> this.CommentToVO(comment, createUserMap, hasThumbCommentSet, thumbNumMap))
                .toList();

        Page<PostCommentVO> commentPage = new Page<>(current, pageSize, total);
        commentPage.setRecords(commentVOList);

        return commentPage;
    }

    @Override
    public int thumbComment(Long firstCommentId, Long secondCommentId) {
        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();

        Comment comment = commentRepository.getCommentById(firstCommentId);
        ThrowUtils.throwIf(comment == null, ErrorCodeEnum.NOT_FOUND_ERROR, "一级评论不存在！");
        long commentId = firstCommentId;
        if (secondCommentId != null) {
            // 点赞二级评论
            Optional<Comment.ReplyComment> secondComment = comment.getReplies().stream()
                    .filter(replyComment -> replyComment.getId().equals(secondCommentId))
                    .findFirst();
            if (secondComment.isEmpty() || secondComment.get().isHasDelete()) {
                throw new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "二级评论不存在！");
            }
            commentId = secondCommentId;
        }

        // 获取点赞信息
        CommentThumb commentThumb = commentThumbRepository.getCommentThumbByCommentId(commentId);
        if (commentThumb == null) {
            // 存入点赞信息
            commentThumb = new CommentThumb();
            commentThumb.setCommentId(commentId);
            Set<Long> userIdSet = new HashSet<>();
            userIdSet.add(currentUser.getId());
            commentThumb.setUserIdList(userIdSet);
            // 保存点赞信息
            commentThumbRepository.save(commentThumb);
            return 1;
        }
        int hasThumb;
        Set<Long> userIdList = commentThumb.getUserIdList();
        if (userIdList.contains(currentUser.getId())) {
            userIdList.remove(currentUser.getId());
            hasThumb = -1;
        } else {
            userIdList.add(currentUser.getId());
            hasThumb = 1;
        }

        commentThumbRepository.save(commentThumb);
        return hasThumb;
    }

    private PostCommentVO CommentToVO(Comment comment, Map<Long, UserBasicInfoBO> createUserMap,
                                      Set<Long> hasThumbCommentSet, Map<Long, Integer> thumbNumMap) {
        // 封装信息
        PostCommentVO postCommentVO = new PostCommentVO();
        BeanUtils.copyProperties(comment, postCommentVO);
        postCommentVO.setCreateUser(createUserMap.get(comment.getUserId()));
        postCommentVO.setHasThumb(hasThumbCommentSet.contains(comment.getId()));
        postCommentVO.setThumbNum(thumbNumMap.getOrDefault(comment.getId(), 0));
        // 封装二级评论
        List<Comment.ReplyComment> replyList = comment.getReplies();
        List<PostCommentVO.ReplyVO> replyVOList = replyList.stream().map(replyComment -> {
            // 删除评论
            if (replyComment.isHasDelete()) {
                PostCommentVO.ReplyVO deleteReplyVO = new PostCommentVO.ReplyVO();
                deleteReplyVO.setId(replyComment.getId());
                deleteReplyVO.setContent("评论已被删除");
                deleteReplyVO.setCreateTime(replyComment.getCreateTime());
                return deleteReplyVO;
            }
            // 正常返回
            PostCommentVO.ReplyVO replyVO = new PostCommentVO.ReplyVO();
            BeanUtils.copyProperties(replyComment, replyVO);
            replyVO.setCreateUser(createUserMap.get(replyComment.getUserId()));
            replyVO.setHasThumb(hasThumbCommentSet.contains(replyComment.getId()));
            replyVO.setThumbNum(thumbNumMap.getOrDefault(replyComment.getId(), 0));
            return replyVO;
        }).toList();

        postCommentVO.setReplyList(replyVOList);
        return postCommentVO;
    }
}

