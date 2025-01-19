package com.adam.post.model.request.image;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 帖子图片添加请求类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/19 13:07
 */
@Data
@Schema(name = "帖子图片添加请求", description = "用户对帖子添加图片请求")
public class PostImageAddRequest implements Serializable {

    /**
     * 帖子id
     */
    @Schema(description = "帖子 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long postId;

    /**
     * 图片
     */
    @Schema(description = "图片列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 9, message = "最多添加 9 张图片")
    private List<String> imageList;

    @Serial
    private static final long serialVersionUID = 1L;
}
