package com.adam.post.service;

import com.adam.post.model.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
