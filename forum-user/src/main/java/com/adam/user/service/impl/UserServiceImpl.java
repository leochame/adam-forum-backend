package com.adam.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.adam.user.model.entity.User;
import com.adam.user.service.UserService;
import com.adam.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author iceman
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-12-12 21:19:05
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




