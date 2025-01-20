package com.adam.user.controller;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.user.model.dto.user.UserAccountRegisterRequest;
import com.adam.user.model.dto.user.UserEditRequest;
import com.adam.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户相关接口
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户相关接口")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户账号注册
     *
     * @param userAccountRegisterRequest 用户注册请求参数
     * @return 用户唯一id
     */
    @PostMapping("/register/account")
    @Operation(summary = "用户账号密码注册")
    public BaseResponse<Long> userAccountRegister(@RequestBody UserAccountRegisterRequest userAccountRegisterRequest) {
        if (userAccountRegisterRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "用户注册参数不能为空！");
        }

        String userAccount = userAccountRegisterRequest.getUserAccount();
        String userPassword = userAccountRegisterRequest.getUserPassword();
        String checkPassword = userAccountRegisterRequest.getCheckPassword();

        long userId = userService.userRegisterByAccount(userAccount, userPassword, checkPassword);

        return ResultUtils.success(userId);

    }

    /**
     * 用户编辑个人信息
     *
     * @param userEditRequest 用户修改内容
     * @return 修改成功
     */
    @PostMapping("/edit")
    @Operation(summary = "用户编辑个人基础信息")
    public BaseResponse<Boolean> editUser(@Valid @RequestBody UserEditRequest userEditRequest) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(userEditRequest), ErrorCodeEnum.PARAMS_ERROR, "用户编辑个人信息不能为空");
        Long userId = userEditRequest.getUserId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "用户 id 错误");
        }

        boolean result = userService.editUser(userEditRequest);

        return ResultUtils.success(result);
    }
}
