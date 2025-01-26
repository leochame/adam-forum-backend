package com.adam.user.controller;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.user.model.request.follow.UserFollowRequest;
import com.adam.user.service.UserFollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/26 18:06
 */
@RestController
@RequestMapping("/user/follow")
@Tag(name = "用户关注相关接口")
@Slf4j
public class UserFollowController {

    @Resource
    private UserFollowService userFollowService;

    /**
     * 关注用户接口
     *
     * @param userFollowRequest 用户关注请求
     * @return 返回 1 成功关注，返回 -1 取消关注
     */
    @PostMapping("/")
    @Operation(summary = "关注用户接口", description = "返回 1 成功关注，返回 -1 取消关注")
    public BaseResponse<Integer> followUser(@RequestBody UserFollowRequest userFollowRequest) {
        ThrowUtils.throwIf(userFollowRequest == null, ErrorCodeEnum.PARAMS_ERROR);
        Long followUserId = userFollowRequest.getFollowedUserId();
        if (followUserId == null || followUserId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "关注用户 id 错误！");
        }

        // 当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();

        int operation = userFollowService.followUser(followUserId, currentUser);

        return ResultUtils.success(operation);
    }


}
