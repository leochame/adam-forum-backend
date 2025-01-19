package com.adam.post.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.post.model.entity.Post;
import com.adam.post.service.PostService;
import com.adam.post.mapper.PostMapper;
import org.springframework.stereotype.Service;

/**
* @author chenjiahan
* @description 针对表【post(帖子表)】的数据库操作Service实现
* @createDate 2025-01-19 13:03:04
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

}




