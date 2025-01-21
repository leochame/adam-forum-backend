package com.adam.post.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 标签 视图类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/21 12:44
 */
@Data
@Schema(name = "标签视图类")
public class TagVO implements Serializable {

    /**
     * id
     */
    @Schema(description = "标签 id")
    private Long id;

    /**
     * 创建用户 id
     */
    @Schema(description = "创建用户 id")
    private Long userId;

    /**
     * 标签名称
     */
    @Schema(description = "标签名称")
    private String name;

    @Serial
    private static final long serialVersionUID = 6153440691965896596L;
}
