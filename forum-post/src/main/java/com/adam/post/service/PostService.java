package com.adam.post.service;

import com.adam.post.model.entity.Post;
import com.adam.post.model.request.post.PostAddRequest;
import com.adam.post.model.request.post.PostEditRequest;
import com.adam.post.model.request.post.PostQueryRequest;
import com.adam.post.model.vo.PostVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 编辑帖子
     *
     * @param postEditRequest 帖子编辑内容
     * @return 编辑成功
     */
    boolean editPost(PostEditRequest postEditRequest);

    /**
     * 删除帖子
     *
     * @param postId 帖子 id
     * @return 删除成功
     */
    boolean deletePost(Long postId);

    /**
     * 根据 id 获取帖子信息
     *
     * @param postId 帖子 id
     * @return 帖子信息
     */
    PostVO getPostVO(Long postId);

    /**
     * 分页帖子获取
     *
     * @param postQueryRequest 帖子搜索请求
     * @return 帖子分页
     */
    Page<PostVO> pagePostVO(PostQueryRequest postQueryRequest);
}
