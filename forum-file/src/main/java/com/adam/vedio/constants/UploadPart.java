package com.adam.vedio.constants;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * 类用于表示视频分片上传的状态和相关数据。
 */
@Data
@Accessors(chain = true)
public class UploadPart {

    /**
     * 存储每个分片的映射关系。
     * 键是分片号，值是对应的文件名。
     */
    private Map<Integer, String> partMap = new HashMap<>();

    /**
     * 已上传的分片总数。
     */
    private Integer totalCount = 0;

    /**
     * 标识是否已经提取了视频封面图。
     */
    private Boolean hasCutImg = false;

    /**
     * 存储提取到的视频封面图的 Base64 编码字符串。
     */
    private String cover = "";
}