package com.adam.post.service;

import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.post.model.entity.PostThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
* @author chenjiahan
* @description 针对表【post_thumb(帖子点赞表)】的数据库操作Service
* @createDate 2025-01-21 17:05:34
*/
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 点赞
     *
     * @param postId    帖子 id
     * @param loginUser 当前登录用户
     * @return 点赞状态
     */
    int doPostThumb(long postId, UserBasicInfoVO loginUser);

    /**
     * 封装了事务的方法
     *
     * @param userId 登录用户 id
     * @param postId 帖子 id
     * @return 点赞状态
     */
    @Transactional(rollbackFor = Exception.class)
    int doPostThumbInner(long userId, long postId);
}
