package com.adam.post.model.request.post;

import com.adam.common.core.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * 帖子分页查询请求类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/21 13:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "帖子分野查询类", description = "用户通过查询参数获取分页信息")
public class PostQueryRequest extends PageRequest implements Serializable {

    /**
     * post id
     */
    @Schema(description = "帖子 id")
    private Long id;

    /**
     * post id 列表
     */
    @Schema(description = "帖子 id 列表")
    private List<Long> ids;

    /**
     * 文本搜索内容
     */
    @Schema(description = "全局文本搜索")
    private String searchText;

    /**
     * 标题搜索
     */
    @Schema(description = "搜索标题")
    private String title;

    /**
     * 内容搜索
     */
    @Schema(description = "内容搜索")
    private String content;

    /**
     * 地理位置搜索
     */
    @Schema(description = "地理位置搜索")
    private String address;

    /**
     * 创作者搜索
     */
    @Schema(description = "创作者搜索")
    private Long userId;

    /**
     * 标签搜索
     */
    @Schema(description = "标签搜索")
    private List<Long> tagIdList;

    @Serial
    private static final long serialVersionUID = -1070038973112042998L;
}
