package com.adam.post.controller;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.request.DeleteRequest;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.post.model.request.post.PostAddRequest;
import com.adam.post.model.request.post.PostEditRequest;
import com.adam.post.model.vo.PostVO;
import com.adam.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "发布帖子")
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
    @Operation(summary = "编辑帖子内容")
    public BaseResponse<Boolean> editPost(@Valid @RequestBody PostEditRequest postEditRequest) {
        ThrowUtils.throwIf(postEditRequest == null, ErrorCodeEnum.PARAMS_ERROR, "编辑帖子内容为空！");
        if (postEditRequest.getId() == null || postEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "帖子 id 错误");
        }

        boolean result = postService.editPost(postEditRequest);

        return ResultUtils.success(result);
    }

    /**
     * 删除帖子接口
     *
     * @param deleteRequest 删除请求
     * @return 删除成功
     */
    @PostMapping("/delete")
    @Operation(summary = "删除帖子")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "删除帖子 id 错误！");
        }

        boolean result = postService.deletePost(deleteRequest.getId());

        return ResultUtils.success(result);
    }


    /**
     * 根据 id 获取帖子 VO
     *
     * @return 帖子 VO
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 id 获取帖子信息")
    public BaseResponse<PostVO> getPostVO(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "获取帖子 id 不能为空");
        }

        PostVO postVO = postService.getPostVO(id);

        return ResultUtils.success(postVO);
    }
}
