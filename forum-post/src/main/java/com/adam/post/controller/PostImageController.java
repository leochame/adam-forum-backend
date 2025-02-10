package com.adam.post.controller;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.post.model.request.image.PostImageUpdateRequest;
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
     * 更新帖子图片列表
     *
     * @param postImageUpdateRequest 更新帖子图片请求类
     * @return 更新成功
     */
    @PostMapping("/update")
    @Operation(summary = "更新帖子图片列表")
    public BaseResponse<Boolean> updatePostImages(@RequestBody PostImageUpdateRequest postImageUpdateRequest) {
        if (postImageUpdateRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "更新帖子图片参数不能为空！");
        }
        Long postId = postImageUpdateRequest.getPostId();
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "帖子 id 参数错误!");
        }

        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();

        boolean result =
                postImageService.updatePostImages(postId, postImageUpdateRequest.getImageList(), currentUser.getId(), postImageUpdateRequest.getCoverIndex());

        return ResultUtils.success(result);
    }
}
