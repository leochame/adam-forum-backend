package com.adam.post.controller;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.core.request.DeleteRequest;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.post.model.request.post.*;
import com.adam.post.model.vo.PostVO;
import com.adam.post.service.PostFavourService;
import com.adam.post.service.PostService;
import com.adam.post.service.PostThumbService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
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

    @Resource
    private PostThumbService postThumbService;

    @Resource
    private PostFavourService postFavourService;

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

    /**
     * 分页搜索帖子信息
     *
     * @param postQueryRequest 帖子搜索请求类
     * @return 帖子分页
     */
    @PostMapping("/page")
    @Operation(summary = "分页搜索帖子信息")
    public BaseResponse<Page<PostVO>> pagePostVO(@RequestBody PostQueryRequest postQueryRequest) {
        long pageSize = postQueryRequest.getPageSize();
        if (pageSize > 50) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "每页最大不超过50");
        }
        Page<PostVO> postVOPage = postService.pagePostVO(postQueryRequest);

        return ResultUtils.success(postVOPage);
    }

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest 帖子点赞请求
     * @return 本次点赞变化数 (1 - 点赞成功/-1 - 取消点赞成功)
     */
    @PostMapping("/thumb")
    @Operation(summary = "点赞 / 取消点赞")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "帖子 id 错误！");
        }
        // 登录才能点赞
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, currentUser);
        return ResultUtils.success(result);
    }

    /**
     * 收藏 / 取消收藏
     *
     * @param postFavourAddRequest 帖子收藏请求
     * @return 本次收藏变化数 (1 - 收藏成功/-1 - 取消收藏成功)
     */
    @PostMapping("/favour")
    @Operation(summary = "收藏 / 取消收藏")
    public BaseResponse<Integer> doPostFavour(@RequestBody PostFavourAddRequest postFavourAddRequest) {
        if (postFavourAddRequest == null || postFavourAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "帖子 id 错误！");
        }
        // 登录才能操作
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        long postId = postFavourAddRequest.getPostId();
        int result = postFavourService.doPostFavour(postId, currentUser);
        return ResultUtils.success(result);
    }
}
