package com.adam.post.model.request.comment;

import com.adam.common.core.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/26 09:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "评论分页查询类", description = "用户通过查询参数获取分页信息")
public class CommentQueryRequest extends PageRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long postId;

    @Serial
    private static final long serialVersionUID = 6818574809744004166L;
}
