package com.adam.post.service.impl;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.post.mapper.PostMapper;
import com.adam.post.model.entity.Post;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.post.model.entity.PostImage;
import com.adam.post.service.PostImageService;
import com.adam.post.mapper.PostImageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【post_image(帖子图片表)】的数据库操作Service实现
 * @createDate 2025-01-19 13:05:23
 */
@Service
public class PostImageServiceImpl extends ServiceImpl<PostImageMapper, PostImage>
        implements PostImageService {

    @Resource
    private PostMapper postMapper;

    @Override
    public boolean addPostImage(Long postId, List<String> imageList) {
        if (CollectionUtils.isEmpty(imageList)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "图片为空！");
        }
        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        // 判断帖子是否存在
        Post post = postMapper.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, postId)
                .select(Post::getUserId)
                .last(DatabaseConstant.LIMIT_ONE));
        ThrowUtils.throwIf(post == null, ErrorCodeEnum.NOT_FOUND_ERROR, "相关帖子信息不存在！");

        // 判断帖子是否为当前登录用户的
        ThrowUtils.throwIf(!post.getUserId().equals(currentUser.getId()), ErrorCodeEnum.NO_AUTH_ERROR, "仅帖子发布者可关联图片");

        // 获取原有图片
        List<PostImage> postImageList = baseMapper.selectList(Wrappers.<PostImage>lambdaQuery()
                .eq(PostImage::getPostId, postId)
                .select(PostImage::getImageOrder));

        // 获取当前图片数量
        AtomicInteger size = new AtomicInteger();
        if (postImageList != null) {
            size.set(postImageList.size());
        }

        // 判断总图片数量是否超过 9 张
        if (size.get() + imageList.size() > 9) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "帖子最多关联 9 张图片");
        }

        // 保存图片
        Set<PostImage> postImages = imageList.stream().map(image -> {
            PostImage postImage = new PostImage();
            postImage.setUserId(currentUser.getId());
            postImage.setPostId(postId);
            postImage.setImage(image);
            postImage.setImageOrder(size.get() + 1);
            size.getAndIncrement();
            return postImage;
        }).collect(Collectors.toSet());

        // 保存数据库
        baseMapper.insert(postImages);
        return true;
    }
}




