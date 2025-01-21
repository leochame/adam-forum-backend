package com.adam.post.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.post.model.entity.PostFavour;
import com.adam.post.service.PostFavourService;
import com.adam.post.mapper.PostFavourMapper;
import org.springframework.stereotype.Service;

/**
* @author chenjiahan
* @description 针对表【post_favour(帖子收藏表)】的数据库操作Service实现
* @createDate 2025-01-21 17:05:34
*/
@Service
public class PostFavourServiceImpl extends ServiceImpl<PostFavourMapper, PostFavour>
    implements PostFavourService{

}




