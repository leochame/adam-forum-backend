package com.adam.post.service;

import com.adam.post.model.entity.PostImage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * @author chenjiahan
 * @description 针对表【post_image(帖子图片表)】的数据库操作Service
 * @createDate 2025-01-19 13:05:23
 */
public interface PostImageService extends IService<PostImage> {

    /**
     * 添加帖子图片
     *
     * @param postId    帖子 id
     * @param imageList 图片列表
     * @return 添加图片成功
     */
    boolean addPostImage(Long postId, List<String> imageList);
}
