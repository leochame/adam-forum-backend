package com.adam.post.service;

import com.adam.post.model.entity.Tag;
import com.adam.post.model.vo.TagVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author chenjiahan
 * @description 针对表【tag(标签表)】的数据库操作Service
 * @createDate 2025-01-19 19:31:39
 */
public interface TagService extends IService<Tag> {

    /**
     * 添加自定义标签
     *
     * @param tagName 标签名称
     * @param userId  创建用户 id
     * @return 标签 id
     */
    long addTag(String tagName, Long userId);

    /**
     * 删除自定义标签
     *
     * @param tagId 标签 id
     * @return 删除成功
     */
    boolean deleteTag(Long tagId);

    /**
     * 关联帖子标签
     *
     * @param postId        帖子 id
     * @param tagIdList     标签 id 列表
     * @param currentUserId 当前登录用户 id
     * @return 关联成功
     */
    boolean associatePostTagList(Long postId, List<Long> tagIdList, Long currentUserId);

    /**
     * 获取帖子下所有 tag 详细列表
     *
     * @param postId 帖子 id
     * @return 标签详细列表
     */
    List<TagVO> getTagVOList(Long postId);
}
