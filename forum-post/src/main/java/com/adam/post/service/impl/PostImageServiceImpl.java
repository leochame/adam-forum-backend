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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【post_image(帖子图片表)】的数据库操作Service实现
 * @createDate 2025-01-19 13:05:23
 */
@Service
@Slf4j
public class PostImageServiceImpl extends ServiceImpl<PostImageMapper, PostImage>
        implements PostImageService {

    @Resource
    private PostMapper postMapper;

    @Override
    public void addPostImage(Long postId, List<String> imageList, int coverIndex) {
        if (CollectionUtils.isEmpty(imageList)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "图片为空！");
        }

        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();

        // 判断总图片数量是否超过 9 张
        if (imageList.size() > 9) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "帖子最多关联 9 张图片");
        }

        if (coverIndex > imageList.size() || coverIndex <= 0) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "选择帖子封面错误");
        }

        // 保存图片
        Set<PostImage> postImages = new HashSet<>();
        for (int index = 0; index < imageList.size(); index++) {
            PostImage postImage = new PostImage();
            postImage.setUserId(currentUser.getId());
            postImage.setPostId(postId);
            postImage.setImage(imageList.get(index));
            postImage.setIsCover(coverIndex == index + 1 ? 1 : 0);
            postImages.add(postImage);
        }

        // 保存数据库
        baseMapper.insert(postImages);

        log.info("新增 {} 张图片，帖子 id {}，封面为第 {} 张图片", imageList.size(), postId, coverIndex);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePostImages(Long postId, List<String> imageList, Long currentUserId, int coverIndex) {
        // 查询原帖信息
        Post post = postMapper.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, postId)
                .select(Post::getUserId)
                .last(DatabaseConstant.LIMIT_ONE));
        ThrowUtils.throwIf(post == null, ErrorCodeEnum.NOT_FOUND_ERROR, "原帖信息不存在，更新图片失败");

        // 判断是否为帖子创建者
        if (!Objects.equals(post.getUserId(), currentUserId)) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "操作失败，仅帖子发布者可更新图片！");
        }

        // 删除原有图片
        int deleteNum = baseMapper.delete(Wrappers.<PostImage>lambdaQuery()
                .eq(PostImage::getPostId, postId));
        log.info("删除 {} 张图片，帖子 id {}", deleteNum, postId);

        // 更新图片信息
        if (!CollectionUtils.isEmpty(imageList)) {
            this.addPostImage(postId, imageList, coverIndex);
        }

        return true;
    }
}




