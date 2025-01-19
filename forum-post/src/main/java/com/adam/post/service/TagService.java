package com.adam.post.service;

import com.adam.post.model.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.Size;

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
}
