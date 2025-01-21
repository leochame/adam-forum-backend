package com.adam.post.model.vo;

import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.service.user.bo.UserBasicInfoBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 帖子 视图类
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/21 12:31
 */
@Data
@Schema(name = "帖子视图类", description = "帖子相关所有信息")
public class PostVO implements Serializable {

    /**
     * id
     */
    @Schema(description = "帖子 id")
    private Long id;

    /**
     * 标题
     */
    @Schema(description = "帖子标题")
    private String title;

    /**
     * 内容
     */
    @Schema(description = "帖子内容")
    private String content;

    /**
     * 发布地点
     */
    @Schema(description = "帖子发布地点")
    private String address;

    /**
     * 点赞数
     */
    @Schema(description = "点赞数")
    private Integer thumbNum;

    /**
     * 收藏数
     */
    @Schema(description = "收藏数")
    private Integer favourNum;


    /**
     * 关联标签列表
     */
    @Schema(description = "帖子关联标签列表")
    private List<TagVO> tagList;

    /**
     * 图片列表
     */
    @Schema(description = "图片列表")
    private List<String> imageList;

    /**
     * 用户信息
     */
    @Schema(description = "帖子创建者基础信息")
    private UserBasicInfoBO createUser;

    @Serial
    private static final long serialVersionUID = 90132243746206497L;
}
