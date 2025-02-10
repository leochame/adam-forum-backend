package com.adam.post.model.request.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/20 19:43
 */
@Data
@Schema(name = "帖子发布请求", description = "用户发布帖子请求")
public class PostAddRequest implements Serializable {

    /**
     * 标题
     */
    @Schema(description = "帖子标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 30, message = "帖子标题长度不能超过 30 个字")
    private String title;

    /**
     * 内容
     */
    @Schema(description = "帖子内容", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 2000, message = "帖子内容长度不能超过 2000 个字")
    private String content;

    /**
     * 发布地点
     */
    @Schema(description = "帖子发布地点", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 50, message = "发布地点信息不能超过 50 个字")
    private String address;

    /**
     * 关联标签列表
     */
    @Schema(description = "帖子关联标签列表", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 20, message = "帖子最多关联 20 个标签")
    private List<Long> tagIdList;

    /**
     * 图片列表
     */
    @Schema(description = "图片列表", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 9, message = "帖子最多关联 9 张图片")
    private List<String> imageList;

    /**
     * 封面图片序列
     */
    @Schema(description = "封面图片序列", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "1")
    private int coverIndex = 1;

    @Serial
    private static final long serialVersionUID = -5786828601757626069L;
}
