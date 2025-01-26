package com.adam.common.core.request;

import com.adam.common.core.constant.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页请求
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    @Schema(description = "当前页号")
    private long current = 1;

    /**
     * 页面大小
     */
    @Schema(description = "页面大小")
    private long pageSize = 5;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String sortField = "create_time";

    /**
     * 排序顺序（默认升序）
     */
    @Schema(description = "排序顺序（默认升序）")
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
