package com.adam.post.controller;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.post.model.request.comment.CommentAddRequest;
import com.adam.post.model.request.comment.CommentDeleteRequest;
import com.adam.post.model.request.comment.CommentQueryRequest;
import com.adam.post.model.request.comment.CommentThumbRequest;
import com.adam.post.model.vo.PostCommentVO;
import com.adam.post.service.PostCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子评论相关接口
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/22 13:35
 */
@RestController
@RequestMapping("/post/comment")
@Slf4j
@Tag(name = "帖子评论相关接口")
public class PostCommentController {

    @Resource
    private PostCommentService postCommentService;

    /**
     * 发布评论接口
     *
     * @param commentAddRequest 帖子评论请求
     * @return 评论 id
     */
    @PostMapping("/add")
    @Operation(summary = "发布评论")
    public BaseResponse<Long> addPostComment(@Valid @RequestBody CommentAddRequest commentAddRequest) {
        if (commentAddRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "评论请求不能为空！");
        }

        Long postId = postCommentService.addComment(commentAddRequest);

        return ResultUtils.success(postId);
    }

    /**
     * 删除评论接口
     *
     * @param commentDeleteRequest 删除请求
     * @return 删除评论数
     */
    @PostMapping("/delete")
    @Operation(summary = "删除评论")
    public BaseResponse<Integer> deletePostComment(@RequestBody CommentDeleteRequest commentDeleteRequest) {
        ThrowUtils.throwIf(commentDeleteRequest == null, ErrorCodeEnum.PARAMS_ERROR, "删除请求不能为空！");
        Long firstCommentId = commentDeleteRequest.getFirstCommentId();
        if (firstCommentId == null || firstCommentId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "删除评论 id 错误！");
        }

        int removeNum = postCommentService.deleteComment(firstCommentId, commentDeleteRequest.getSecondCommentId());

        return ResultUtils.success(removeNum);
    }

    /**
     * 根据 id 获取评论信息
     *
     * @param commentId 评论 id
     * @return 评论信息
     */
    @GetMapping("/get/vo/{commentId}")
    @Operation(summary = "根据 id 获取评论信息")
    public BaseResponse<PostCommentVO> getPostCommentVO(@PathVariable("commentId") Long commentId) {
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "评论 id 错误！");
        }

        PostCommentVO postCommentVO = postCommentService.getCommentVOById(commentId);

        return ResultUtils.success(postCommentVO);
    }

    /**
     * 分页获取评论信息
     *
     * @param commentQueryRequest 查询参数
     * @return 评论信息分页
     */
    @PostMapping("/page/vo")
    @Operation(summary = "分页获取评论信息")
    public BaseResponse<Page<PostCommentVO>> pagePostCommentVO(@RequestBody CommentQueryRequest commentQueryRequest) {
        long pageSize = commentQueryRequest.getPageSize();
        if (pageSize > 20) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "获取数量过多，单页数量不能超过 20");
        }

        Page<PostCommentVO> commentPage = postCommentService.pageCommentVO(commentQueryRequest);

        return ResultUtils.success(commentPage);
    }

    /**
     * 评论点赞
     *
     * @param commentThumbRequest 点赞请求
     * @return 点赞状态
     */
    @PostMapping("/thumb")
    @Operation(summary = "评论点赞")
    public BaseResponse<Integer> thumbComment(@RequestBody CommentThumbRequest commentThumbRequest) {
        ThrowUtils.throwIf(commentThumbRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        Long firstCommentId = commentThumbRequest.getFirstCommentId();
        Long secondCommentId = commentThumbRequest.getSecondCommentId();
        if (firstCommentId == null || firstCommentId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "一级评论 id 错误");
        }

        int thumbResult = postCommentService.thumbComment(firstCommentId, secondCommentId);

        return ResultUtils.success(thumbResult);
    }
}
