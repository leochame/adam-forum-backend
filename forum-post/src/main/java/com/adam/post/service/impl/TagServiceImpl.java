package com.adam.post.service.impl;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.database.constant.DatabaseConstant;
import com.adam.post.mapper.PostMapper;
import com.adam.post.mapper.PostTagMapper;
import com.adam.post.mapper.TagMapper;
import com.adam.post.model.entity.Post;
import com.adam.post.model.entity.PostTag;
import com.adam.post.model.entity.Tag;
import com.adam.post.service.TagService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【tag(标签表)】的数据库操作Service实现
 * @createDate 2025-01-19 19:31:39
 */
@Service
@Slf4j
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
        implements TagService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostTagMapper postTagMapper;


    @Override
    public long addTag(String tagName, Long userId) {
        // 判断是否重复创建相同标签
        boolean exists = baseMapper.exists(Wrappers.<Tag>lambdaQuery()
                .eq(Tag::getName, tagName));
        ThrowUtils.throwIf(exists, ErrorCodeEnum.OPERATION_ERROR, "tag已存在，请勿重复创建");

        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setUserId(userId);
        baseMapper.insert(tag);

        return tag.getId();
    }

    @Override
    public boolean deleteTag(Long tagId) {
        boolean result = baseMapper.delete(Wrappers.<Tag>lambdaQuery()
                .eq(Tag::getId, tagId)) != 0;
        ThrowUtils.throwIf(!result, ErrorCodeEnum.NOT_FOUND_ERROR, "删除 tag 已不存在，请勿重复操作");
        return true;
    }

    @Override
    public boolean associatePostTagList(Long postId, List<Long> tagIdList, Long currentUserId) {
        // 查询帖子信息
        Post post = postMapper.selectOne(Wrappers.<Post>lambdaQuery()
                .eq(Post::getId, postId)
                .select(Post::getUserId)
                .last(DatabaseConstant.LIMIT_ONE));
        ThrowUtils.throwIf(post == null, ErrorCodeEnum.NOT_FOUND_ERROR, "帖子信息不存在！");

        // 判断帖子创建者
        if (!Objects.equals(post.getUserId(), currentUserId)) {
            throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR, "仅帖子创建者能关联标签");
        }

        List<PostTag> postTagList = postTagMapper.selectList(Wrappers.<PostTag>lambdaQuery()
                .eq(PostTag::getPostId, postId)
                .select(PostTag::getTagId));
        // 原有 tagId 列表
        Set<Long> originalTagIdSet = postTagList.stream()
                .map(PostTag::getTagId)
                .collect(Collectors.toSet());

        // 去除重复 tag id
        Set<PostTag> postTagSet = tagIdList.stream()
                .filter(tagId -> !originalTagIdSet.contains(tagId))
                .map(tagId -> {
                    PostTag postTag = new PostTag();
                    postTag.setPostId(postId);
                    postTag.setTagId(tagId);
                    return postTag;
                })
                .collect(Collectors.toSet());

        if (postTagSet.size() + tagIdList.size() > 20) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "帖子最多关联 20 个标签");
        }

        postTagMapper.insert(postTagSet);
        log.info("关联 post {} 标签 {} 个", postId, postTagSet.size());
        return true;
    }
}




