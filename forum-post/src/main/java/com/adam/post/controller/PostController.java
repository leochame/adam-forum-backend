package com.adam.post.controller;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.post.model.request.post.PostAddRequest;
import com.adam.post.model.request.post.PostEditRequest;
import com.adam.post.service.PostService;
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
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/18 14:55
 */
@RestController
@RequestMapping("/post")
@Tag(name = "帖子相关接口")
@Slf4j
public class PostController {

    @Resource
    private PostService postService;

    /**
     * 用户发布帖子接口
     *
     * @param postAddRequest 发布帖子内容
     * @return 帖子 id
     */
    @PostMapping("/add")
    @Operation(summary = "发布帖子接口")
    public BaseResponse<Long> addPost(@Valid @RequestBody PostAddRequest postAddRequest) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "发布帖子内容为空！");
        }

        long postId = postService.addPost(postAddRequest);

        return ResultUtils.success(postId);
    }

    /**
     * 编辑帖子内容接口
     *
     * @param postEditRequest 帖子编辑请求
     * @return 编辑成功
     */
    @PostMapping("/edit")
    @Operation(summary = "编辑帖子内容接口")
    public BaseResponse<Boolean> editPost(@Valid @RequestBody PostEditRequest postEditRequest) {
        ThrowUtils.throwIf(postEditRequest == null, ErrorCodeEnum.PARAMS_ERROR, "编辑帖子内容为空！");
        if (postEditRequest.getId() == null || postEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "帖子 id 错误");
        }

        boolean result = postService.editPost(postEditRequest);

        return ResultUtils.success(result);
    }
}
