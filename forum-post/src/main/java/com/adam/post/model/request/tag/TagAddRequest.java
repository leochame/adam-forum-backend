package com.adam.post.model.request.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/19 19:35
 */
@Data
@Schema(name = "tag 添加请求类", description = "用户添加自定义标签")
public class TagAddRequest {

    /**
     * 帖子名称
     */
    @Schema(description = "tag 名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 8, message = "标签长度不能超过 30")
    private String name;
}
