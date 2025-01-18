package com.adam.post.controller;

import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.service.user.bo.UserBasicInfoBO;
import com.adam.service.user.service.UserBasicRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/18 14:55
 */
@RestController
@RequestMapping("/post")
public class PostController {
    @DubboReference
    private UserBasicRpcService userBasicRpcService;

    @GetMapping("/demo/{id}")
    public BaseResponse<UserBasicInfoBO> getDemoUser(@PathVariable("id") Long id) {
        UserBasicInfoBO userBasicInfoBO = userBasicRpcService.getUserBasicInfoByUserId(id);
        return ResultUtils.success(userBasicInfoBO);
    }
}
