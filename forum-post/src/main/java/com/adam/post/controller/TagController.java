package com.adam.post.controller;

import com.adam.common.auth.annotation.CheckRole;
import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.enums.UserRoleEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.core.request.DeleteRequest;
import com.adam.common.core.response.BaseResponse;
import com.adam.common.core.response.ResultUtils;
import com.adam.post.model.request.tag.TagAddRequest;
import com.adam.post.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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


    /**
     * 删除自定义标签 (管理员)
     *
     * @param deleteRequest 删除请求
     * @return 删除成功
     */
    @PostMapping("/delete")
    @Operation(summary = "管理员删除自定义 tag")
    @CheckRole(mustRole = {UserRoleEnum.ADMIN, UserRoleEnum.SUPER_ADMIN})
    public BaseResponse<Boolean> deleteTag(@RequestBody DeleteRequest deleteRequest) {
        Long tagId = deleteRequest.getId();
        if (tagId == null || tagId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "删除 tagId 错误");
        }

        boolean result = tagService.deleteTag(tagId);

        return ResultUtils.success(result);
    }
}
