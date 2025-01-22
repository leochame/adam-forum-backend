package com.adam.post.controller;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.post.model.request.comment.CommentAddRequest;
import com.adam.post.service.PostCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 发布帖子评论接口
     *
     * @param commentAddRequest 帖子评论请求
     * @return 评论 id
     */
    @PostMapping("/add")
    @Operation(summary = "发布帖子评论")
    public BaseResponse<Long> addPostComment(@Valid @RequestBody CommentAddRequest commentAddRequest) {
        if (commentAddRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "评论请求不能为空！");
        }

        Long postId = postCommentService.addComment(commentAddRequest);

        return ResultUtils.success(postId);
    }
}
