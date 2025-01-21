package com.adam.post.service;

import com.adam.post.model.entity.Post;
import com.adam.post.model.request.post.PostAddRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author chenjiahan
 * @description 针对表【post(帖子表)】的数据库操作Service
 * @createDate 2025-01-19 13:03:04
 */
public interface PostService extends IService<Post> {

    /**
     * 发布帖子
     *
     * @param postAddRequest 帖子发布信息
     * @return 帖子 id
     */
    @Transactional(rollbackFor = Exception.class)
    long addPost(PostAddRequest postAddRequest);
}
