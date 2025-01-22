package com.adam.post.service.impl;

import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.post.model.entity.Post;
import com.adam.post.service.PostService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.post.model.entity.PostThumb;
import com.adam.post.service.PostThumbService;
import com.adam.post.mapper.PostThumbMapper;
import jakarta.annotation.Resource;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author chenjiahan
 * @description 针对表【post_thumb(帖子点赞表)】的数据库操作Service实现
 * @createDate 2025-01-21 17:05:34
 */
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
        implements PostThumbService {

    @Resource
    private PostService postService;

    @Override
    public int doPostThumb(long postId, UserBasicInfoVO loginUser) {
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "原帖不存在，请刷新重试！");
        }

        // 是否已点赞
        long userId = loginUser.getId();

        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
        PostThumbService postThumbService = (PostThumbService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return postThumbService.doPostThumbInner(userId, postId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostThumbInner(long userId, long postId) {
        PostThumb postThumb = new PostThumb();
        postThumb.setUserId(userId);
        postThumb.setPostId(postId);
        QueryWrapper<PostThumb> thumbQueryWrapper = new QueryWrapper<>(postThumb);
        PostThumb oldPostThumb = this.getOne(thumbQueryWrapper);
        boolean result;
        // 已点赞
        if (oldPostThumb != null) {
            result = this.remove(thumbQueryWrapper);
            if (result) {
                // 点赞数 - 1
                result = postService.update()
                        .eq("id", postId)
                        .gt("thumb_num", 0)
                        .setSql("thumb_num = thumb_num - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR);
            }
        } else {
            // 未点赞
            result = this.save(postThumb);
            if (result) {
                // 点赞数 + 1
                result = postService.update()
                        .eq("id", postId)
                        .setSql("thumb_num = thumb_num + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR);
            }
        }
    }
}




