package com.adam.post.controller;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.post.model.request.image.PostImageAddRequest;
import com.adam.post.service.PostImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 帖子图片相关接口
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/19 13:06
 */
@RestController
@RequestMapping("/post/image")
@Tag(name = "帖子图片相关接口")
@Slf4j
public class PostImageController {

    @Resource
    private PostImageService postImageService;

    /**
     * 添加帖子图片
     *
     * @param postImageAddRequest 帖子图片添加请求类
     * @return 添加图片成功
     */
    @PostMapping("/add")
    @Operation(summary = "帖子关联图片")
    public BaseResponse<Boolean> addPostImages(@RequestBody PostImageAddRequest postImageAddRequest) {
        ThrowUtils.throwIf(postImageAddRequest == null, ErrorCodeEnum.PARAMS_ERROR, "关联帖子图片不能为空！");
        Long postId = postImageAddRequest.getPostId();
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "帖子 id 错误！");
        }

        boolean result = postImageService.addPostImage(postId, postImageAddRequest.getImageList());

        return ResultUtils.success(result);
    }
}
