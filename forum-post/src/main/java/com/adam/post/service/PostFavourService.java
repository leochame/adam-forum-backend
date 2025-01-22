package com.adam.post.service;

import com.adam.common.core.model.vo.UserBasicInfoVO;
import com.adam.post.model.entity.PostFavour;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author chenjiahan
* @description 针对表【post_favour(帖子收藏表)】的数据库操作Service
* @createDate 2025-01-21 17:05:34
*/
public interface PostFavourService extends IService<PostFavour> {

    /**
     * 帖子收藏
     *
     * @param postId    帖子 id
     * @param loginUser 当前登录用户
     * @return 收藏状态
     */
    int doPostFavour(long postId, UserBasicInfoVO loginUser);


    /**
     * 帖子收藏（内部服务）
     *
     * @param userId 用户 id
     * @param postId 帖子 id
     * @return 收藏状态
     */
    int doPostFavourInner(long userId, long postId);
}
