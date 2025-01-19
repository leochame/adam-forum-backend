package com.adam.post.service.impl;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.ThrowUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.post.model.entity.Tag;
import com.adam.post.service.TagService;
import com.adam.post.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
 * @author chenjiahan
 * @description 针对表【tag(标签表)】的数据库操作Service实现
 * @createDate 2025-01-19 19:31:39
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
        implements TagService {

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
}




