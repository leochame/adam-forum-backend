package com.adam.post.controller;

import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.post.model.request.post.PostAddRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PostMapping("/add")
    public BaseResponse<Long> addPost(@Valid @RequestBody PostAddRequest postAddRequest) {
        return ResultUtils.success(1L);
    }
}
