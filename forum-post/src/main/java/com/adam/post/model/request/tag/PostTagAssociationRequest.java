package com.adam.post.model.request.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * tag 关联帖子类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/19 21:53
 */
@Data
@Schema(name = "tag 关联帖子类", description = "用户添加帖子的tag")
public class PostTagAssociationRequest implements Serializable {

    /**
     * 帖子id
     */
    @Schema(description = "帖子 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long postId;

    /**
     * 标签 id 列表
     */
    @Schema(description = "标签 id 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> tagIdList;

    @Serial
    private static final long serialVersionUID = 3740330397716514606L;
}
