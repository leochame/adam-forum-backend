package com.adam.post.service.impl;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.post.model.request.post.PostAddRequest;
import com.adam.post.service.PostImageService;
import com.adam.post.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.post.model.entity.Post;
import com.adam.post.service.PostService;
import com.adam.post.mapper.PostMapper;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author chenjiahan
 * @description 针对表【post(帖子表)】的数据库操作Service实现
 * @createDate 2025-01-19 13:03:04
 */
@Service
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
        implements PostService {

    private static final Gson GSON = new Gson();

    @Resource
    private PostImageService postImageService;

    @Resource
    private TagService tagService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addPost(PostAddRequest postAddRequest) {
        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();

        // 保存帖子信息
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
        post.setUserId(currentUser.getId());
        baseMapper.insert(post);

        // 保存图片信息
        List<String> imageList = postAddRequest.getImageList();
        if (!CollectionUtils.isEmpty(imageList)) {
            postImageService.addPostImage(post.getId(), imageList);
        }

        // 保存标签信息
        List<Long> tagIdList = postAddRequest.getTagIdList();
        if (!CollectionUtils.isEmpty(tagIdList)) {
            tagService.associatePostTagList(post.getId(), tagIdList, currentUser.getId());
        }

        log.info("发布帖子成功 「{}」，创建用户信息：「{}」", GSON.toJson(post), GSON.toJson(currentUser));
        return post.getId();

    }
}




