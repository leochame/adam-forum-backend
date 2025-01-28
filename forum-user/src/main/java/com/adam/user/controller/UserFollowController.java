package com.adam.user.controller;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.service.user.bo.UserBasicInfoBO;
import com.adam.user.model.enums.FollowEnum;
import com.adam.user.model.request.follow.UserFollowRequest;
import com.adam.user.service.UserFollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 获取关注 / 粉丝列表
     *
     * @return 关注列表
     */
    @GetMapping("/list/followed/{type}")
    @Operation(summary = "获取关注 / 粉丝列表")
    public BaseResponse<List<UserBasicInfoBO>> listFollowed(@PathVariable("type") FollowEnum followEnum) {
        log.info("debug");
        List<UserBasicInfoBO> userBasicInfoBOList = userFollowService.listFollow(followEnum);
        return ResultUtils.success(userBasicInfoBOList);
    }
}
