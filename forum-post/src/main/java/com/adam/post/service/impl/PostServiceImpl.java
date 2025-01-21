package com.adam.post.service.impl;

import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.post.mapper.PostMapper;
import com.adam.post.mapper.PostTagMapper;
import com.adam.post.model.entity.Post;
import com.adam.post.model.entity.PostImage;
import com.adam.post.model.entity.PostTag;
import com.adam.post.model.request.post.PostAddRequest;
import com.adam.post.model.request.post.PostEditRequest;
import com.adam.post.model.vo.PostVO;
import com.adam.post.model.vo.TagVO;
import com.adam.post.service.PostImageService;
import com.adam.post.service.PostService;
import com.adam.post.service.TagService;
import com.adam.service.user.bo.UserBasicInfoBO;
import com.adam.service.user.service.UserBasicRpcService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

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
    private TransactionTemplate transactionTemplate;

    @Resource
    private TagService tagService;

    @Resource
    private PostTagMapper postTagMapper;

    @DubboReference
    private UserBasicRpcService userBasicRpcService;

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

    @Override
    public boolean editPost(PostEditRequest postEditRequest) {
        // 判断是否是创建者
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        Post originalPost = baseMapper.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, postEditRequest.getId())
                .select(Post::getUserId));
        ThrowUtils.throwIf(originalPost == null, ErrorCodeEnum.NOT_FOUND_ERROR, "原帖已不存在！");

        if (!Objects.equals(originalPost.getUserId(), currentUser.getId())) {
            throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR, "仅帖子创作者可编辑帖子内容");
        }

        Post post = new Post();
        BeanUtils.copyProperties(postEditRequest, post);
        log.info("更新帖子成功 「{}」，创建用户信息：「{}」", GSON.toJson(postEditRequest), GSON.toJson(currentUser));
        return true;
    }

    @Override
    public boolean deletePost(Long postId) {
        // 获取当前登录用户
        UserBasicInfoVO currentUser = SecurityContext.getCurrentUser();
        Long userId = currentUser.getId();
        Post originalPost = baseMapper.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, postId)
                .select(Post::getUserId));
        ThrowUtils.throwIf(originalPost == null, ErrorCodeEnum.NOT_FOUND_ERROR, "原帖已不存在！");

        if (!Objects.equals(originalPost.getUserId(), userId)) {
            throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR, "仅帖子创作者可删除帖子内容");
        }

        // 事务删除所有信息
        transactionTemplate.execute(status -> {
            // 删除原帖信息
            baseMapper.deleteById(postId);

            // 删除帖子图片信息
            postImageService.remove(Wrappers.<PostImage>lambdaQuery()
                    .eq(PostImage::getPostId, postId));

            // 删除帖子标签信息
            postTagMapper.delete(Wrappers.<PostTag>lambdaQuery()
                    .eq(PostTag::getPostId, postId));
            return Boolean.TRUE;

            // todo 删除点赞信息
        });

        log.info("删除 帖子「{}」，创建者 「{}」", postId, GSON.toJson(currentUser));

        return true;
    }

    @Override
    public PostVO getPostVO(Long postId) {
        // todo 缓存改造
        Post post = baseMapper.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, postId)
                .last(DatabaseConstant.LIMIT_ONE));
        ThrowUtils.throwIf(post == null, ErrorCodeEnum.NOT_FOUND_ERROR, "帖子信息不存在");

        PostVO postVO = new PostVO();
        BeanUtils.copyProperties(post, postVO);

        // 获取图片
        List<PostImage> postImageList = postImageService.list(Wrappers.<PostImage>lambdaQuery()
                .eq(PostImage::getPostId, postId)
                .select(PostImage::getImage));
        List<String> imageList = postImageList.stream().map(PostImage::getImage).toList();
        postVO.setImageList(imageList);

        // 获取标签
        List<TagVO> tagVOList = tagService.getTagVOList(postId);
        postVO.setTagList(tagVOList);

        // 获取帖子创建者信息
        UserBasicInfoBO userBasicInfoBO = userBasicRpcService.getUserBasicInfoByUserId(post.getUserId());
        postVO.setCreateUser(userBasicInfoBO);

        return postVO;
    }
}