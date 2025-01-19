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

        // 判断总图片数量是否超过 9 张
        if (imageList.size() > 9) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "帖子最多关联 9 张图片");
        }

        // 保存图片
        Set<PostImage> postImages = imageList.stream().map(image -> {
            PostImage postImage = new PostImage();
            postImage.setUserId(currentUser.getId());
            postImage.setPostId(postId);
            postImage.setImage(image);
            return postImage;
        }).collect(Collectors.toSet());

        // 保存数据库
        baseMapper.insert(postImages);
        return true;
    }
}




