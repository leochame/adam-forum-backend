package com.adam.auth.controller;

import com.adam.auth.model.dto.UserPasswordLoginRequest;
import com.adam.auth.service.UserService;
import com.adam.common.cache.service.RedisCacheService;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.TokenVO;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.common.core.utils.TokenUtil;
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
 * @create 2024/12/14 12:10
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "登录相关接口")
@Slf4j
public class LoginController {

    @Resource
    private UserService userService;

    @Resource
    private RedisCacheService redisCacheService;

    @PostMapping("/login/password")
    @Operation(summary = "用户账号密码登录")
    public BaseResponse<TokenVO> userPasswordLogin(@RequestBody UserPasswordLoginRequest userPasswordLoginRequest) {
        if (userPasswordLoginRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "用户账号密码为空!");
        }

        String userAccount = userPasswordLoginRequest.getUserAccount();
        String userPassword = userPasswordLoginRequest.getUserPassword();

        // 判断登录凭证
        UserBasicInfoVO userBasicInfoVO = userService.userLoginByUserAccountAndPassword(userAccount, userPassword);

        // 生成 token 信息
        TokenVO tokenVO = TokenUtil.createTokenVO(userBasicInfoVO.getId(), userAccount);

        // 将 token 信息存储到缓存中
        redisCacheService.storeToken(userBasicInfoVO, tokenVO);
        log.info("User {} Login Successfully!", userBasicInfoVO.getId());

        return ResultUtils.success(tokenVO);
    }


}
