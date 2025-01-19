package com.adam.post.controller;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.post.model.request.tag.TagAddRequest;
import com.adam.post.service.TagService;
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
 * @create 2025/1/19 19:32
 */
@RestController
@RequestMapping("/post/tag")
@Tag(name = "标签相关接口")
@Slf4j
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 添加自定义标签
     *
     * @param tagAddRequest 标签添加请求类
     * @return 标签 id
     */
    @PostMapping("/add")
    @Operation(summary = "添加自定义 tag")
    public BaseResponse<Long> addTag(@RequestBody TagAddRequest tagAddRequest) {
        if (tagAddRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "新增标签不能为空！");
        }

        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        long tagId = tagService.addTag(tagAddRequest.getName(), currentUser.getId());

        return ResultUtils.success(tagId);
    }
}
