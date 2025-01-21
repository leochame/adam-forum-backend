package com.adam.post.service;

import com.adam.post.model.entity.PostImage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author chenjiahan
 * @description 针对表【post_image(帖子图片表)】的数据库操作Service
 * @createDate 2025-01-19 13:05:23
 */
public interface PostImageService extends IService<PostImage> {

    /**
     * 添加帖子图片（内部使用）
     *
     * @param postId    帖子 id
     * @param imageList 图片列表
     */
    void addPostImage(Long postId, List<String> imageList);

    /**
     * 更新帖子图片
     *
     * @param postId        帖子 id
     * @param imageList     图片列表
     * @param currentUserId 当前登录用户 id
     * @return 更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updatePostImages(Long postId, List<String> imageList, Long currentUserId);
}
