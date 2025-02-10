package com.adam.post.model.request.image;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 帖子图片更新请求类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/19 20:29
 */
@Data
@Schema(name = "帖子图片更新请求", description = "用户更新帖子图片关联")
public class PostImageUpdateRequest implements Serializable {

    /**
     * 帖子id
     */
    @Schema(description = "帖子 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long postId;

    /**
     * 图片
     */
    @Schema(description = "图片列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> imageList;

    /**
     * 封面图片序列
     */
    @Schema(description = "封面图片序列", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "1")
    private int coverIndex = 1;

    @Serial
    private static final long serialVersionUID = 8828236698948741357L;
}
